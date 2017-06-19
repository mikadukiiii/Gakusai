/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.physical_web.physicalweb;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.RemoteViews;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.uribeacon.beacon.UriBeacon;
import org.uribeacon.scan.compat.BluetoothLeScannerCompat;
import org.uribeacon.scan.compat.BluetoothLeScannerCompatProvider;
import org.uribeacon.scan.compat.ScanCallback;
import org.uribeacon.scan.compat.ScanFilter;
import org.uribeacon.scan.compat.ScanResult;
import org.uribeacon.scan.compat.ScanSettings;
import org.uribeacon.scan.util.RegionResolver;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import it.poliba.sisinflab.physicalweb.AnnotationDownloader;
import it.poliba.sisinflab.physicalweb.SemanticData;
import it.poliba.sisinflab.physicalweb.SemanticReasoner;
import it.poliba.sisinflab.physicalweb.UserData;

/**
 * This is a service that scans for nearby uriBeacons.
 * It is created by MainActivity.
 * It finds nearby ble beacons,
 * and stores a count of them.
 * It also listens for screen on/off events
 * and start/stops the scanning accordingly.
 * It also silently issues a notification
 * informing the user of nearby beacons.
 * As beacons are found and lost,
 * the notification is updated to reflect
 * the current number of nearby beacons.
 */

public class UriBeaconSemanticDiscoveryService extends Service implements InnerMetadataResolver.InnerMetadataResolverCallback, AnnotationDownloader.AnnotationDownloaderCallback, MetadataResolver.MetadataResolverCallback, MdnsUrlDiscoverer.MdnsUrlDiscovererCallback {

    private static final String TAG = "SemanticDiscoveryServ";
    private final ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult scanResult) {
            switch (callbackType) {
                case ScanSettings.CALLBACK_TYPE_FIRST_MATCH:
                    handleFoundDevice(scanResult);
                    break;
                case ScanSettings.CALLBACK_TYPE_ALL_MATCHES:
                    handleFoundDevice(scanResult);
                    break;
                default:
                    Log.e(TAG, "Unrecognized callback type constant received: " + callbackType);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.d(TAG, "onScanFailed  " + "errorCode: " + errorCode);
        }
    };
    private static final String NOTIFICATION_GROUP_KEY = "URI_BEACON_NOTIFICATIONS";
    private static final int NEAREST_BEACON_NOTIFICATION_ID = 23;
    private static final int SECOND_NEAREST_BEACON_NOTIFICATION_ID = 24;
    private static final int SUMMARY_NOTIFICATION_ID = 25;
    private static final int TIMEOUT_FOR_OLD_BEACONS = 2;
    private static final int NON_LOLLIPOP_NOTIFICATION_TITLE_COLOR = Color.parseColor("#ffffff");
    private static final int NON_LOLLIPOP_NOTIFICATION_URL_COLOR = Color.parseColor("#999999");
    private static final int NON_LOLLIPOP_NOTIFICATION_SNIPPET_COLOR = Color.parseColor("#999999");
    private static final int NOTIFICATION_PRIORITY = NotificationCompat.PRIORITY_LOW;
    private static final long NOTIFICATION_UPDATE_GATE_DURATION = 1000;
    private boolean mCanUpdateNotifications = false;
    private Handler mHandler;
    private ScreenBroadcastReceiver mScreenStateBroadcastReceiver;
    private RegionResolver mRegionResolver;
    private NotificationManagerCompat mNotificationManager;
    private HashMap<String, MetadataResolver.UrlMetadata> mUrlToUrlMetadata;
    private HashMap<String, SemanticData> mUrlToSemantic;
    private List<String> mSortedDevices;
    private HashMap<String, String> mDeviceAddressToUrl;
    private MdnsUrlDiscoverer mMdnsUrlDiscoverer;

    private SemanticReasoner semanticReasoner;
    private UserData userData;
    // Sort using local region-resolver regions
    private Comparator<String> mSortByRegionResolverRegionComparator = new Comparator<String>() {
        @Override
        public int compare(String address, String otherAddress) {
            // Check if one of the addresses is the nearest
            final String nearest = mRegionResolver.getNearestAddress();
            if (address.equals(nearest)) {
                return -1;
            }
            if (otherAddress.equals(nearest)) {
                return 1;
            }
            // Otherwise sort by region
            int r1 = mRegionResolver.getRegion(address);
            int r2 = mRegionResolver.getRegion(otherAddress);
            if (r1 != r2) {
                return ((Integer) r1).compareTo(r2);
            }
            // The two devices are in the same region, sort by device address.
            return address.compareTo(otherAddress);
        }
    };
    // Sort using local proxy server scores
    private Comparator<String> mSortByProxyServerScoreComparator = new Comparator<String>() {
        @Override
        public int compare(String addressA, String addressB) {
            String urlA = mDeviceAddressToUrl.get(addressA);
            String urlB = mDeviceAddressToUrl.get(addressB);
            MetadataResolver.UrlMetadata urlMetadataA = mUrlToUrlMetadata.get(urlA);
            MetadataResolver.UrlMetadata urlMetadataB = mUrlToUrlMetadata.get(urlB);
            // If metadata exists for both urls
            if ((urlMetadataA != null) && (urlMetadataB != null)) {
                float scoreA = urlMetadataA.score;
                float scoreB = urlMetadataB.score;
                // If the scores are not equal
                if (scoreA != scoreB) {
                    // Sort so that higher scores show up higher in the list
                    return ((Float) scoreB).compareTo(scoreA);
                }
                // The scores are equal so sort by metadata title
                String titleA = urlMetadataA.title;
                String titleB = urlMetadataB.title;
                return titleA.compareTo(titleB);
            }
            // Sort the url with metadata to be first
            if (urlMetadataA == null) {
                return 1;
            }
            return -1;
        }
    };

    private Comparator<String> mSortBySemanticScoreComparator = new Comparator<String>() {
        @Override
        public int compare(String addressA, String addressB) {
            String urlA = mDeviceAddressToUrl.get(addressA);
            String urlB = mDeviceAddressToUrl.get(addressB);
            SemanticData dataA = mUrlToSemantic.get(urlA);
            SemanticData dataB = mUrlToSemantic.get(urlB);

            // If metadata exists for both urls
            if ((dataA != null) && (dataB != null)) {
                double scoreA = dataA.getPenalty();
                double scoreB = dataB.getPenalty();
                // If the scores are not equal

                // Sort so that higher scores show up higher in the list
                return -((Double) scoreA).compareTo(scoreB);


            }

            // Sort the url with metadata to be first
            if (dataA == null) {
                return 1;
            }
            return -1;
        }
    };

    // TODO: consider a more elegant solution for preventing notification conflicts
    private Runnable mNotificationUpdateGateTimeout = new Runnable() {
        @Override
        public void run() {
            mCanUpdateNotifications = true;
            updateNotifications();
        }
    };

    public UriBeaconSemanticDiscoveryService() {
    }

    private static String generateMockBluetoothAddress(int hashCode) {
        String mockAddress = "00:11";
        for (int i = 0; i < 4; i++) {
            mockAddress += String.format(":%02X", hashCode & 0xFF);
            hashCode = hashCode >> 8;
        }
        return mockAddress;
    }

    private void initialize() {

        semanticReasoner = new SemanticReasoner();
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            //String onto_path = prefs.getString(getResources().getString(R.string.ontology_path_key), SemanticReasoner.DEFAULT_ONTO);
            String req_path = prefs.getString(getResources().getString(R.string.request_path_key), SemanticReasoner.DEFAULT_REQ);
            semanticReasoner.loadOntology(req_path);
          /*Intent intent = new Intent(getActivity(), BuildSemanticDescription.class);
          intent.putExtra("onto_path", SemanticReasoner.DEFAULT_ONTO);
        startActivity(intent);*/
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }

        userData = UserData.load(this);
        mNotificationManager = NotificationManagerCompat.from(this);
        mMdnsUrlDiscoverer = new MdnsUrlDiscoverer(this, UriBeaconSemanticDiscoveryService.this);
        mHandler = new Handler();
        initializeScreenStateBroadcastReceiver();
    }

    private void initializeLists() {
        mRegionResolver = new RegionResolver();
        mUrlToUrlMetadata = new HashMap<>();
        mUrlToSemantic = new HashMap<>();
        mSortedDevices = null;
        mDeviceAddressToUrl = new HashMap<>();
    }

    /**
     * Create the broadcast receiver that will listen
     * for screen on/off events
     */
    private void initializeScreenStateBroadcastReceiver() {
        mScreenStateBroadcastReceiver = new ScreenBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenStateBroadcastReceiver, intentFilter);
    }

    private BluetoothLeScannerCompat getLeScanner() {
        return BluetoothLeScannerCompatProvider.getBluetoothLeScannerCompat(getApplicationContext());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initialize();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Since sometimes the lists have values when onStartCommand gets called
        initializeLists();
        // Start scanning only if the screen is on
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager.isScreenOn()) {
            mCanUpdateNotifications = false;
            mHandler.postDelayed(mNotificationUpdateGateTimeout, NOTIFICATION_UPDATE_GATE_DURATION);
            startSearchingForUriBeacons();
            //mMdnsUrlDiscoverer.startScanning();
        }

        //make sure the service keeps running
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy:  service exiting");
        mHandler.removeCallbacks(mNotificationUpdateGateTimeout);
        stopSearchingForUriBeacons();
        try {
            //mMdnsUrlDiscoverer.stopScanning();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        unregisterReceiver(mScreenStateBroadcastReceiver);
        mNotificationManager.cancelAll();
    }

    @Override
    public void onUrlMetadataReceived(String url, MetadataResolver.UrlMetadata urlMetadata) {
        mUrlToUrlMetadata.put(url, urlMetadata);
        updateNotifications();
        AnnotationDownloader.downloadAnnotation(this, UriBeaconSemanticDiscoveryService.this, url, urlMetadata.siteUrl);
    }

    @Override
    public void onAnnotationDownloaded(final String url, final String annotation) {

        new AsyncTask<Object, Void, Void>() {
            HashMap<String, Object> supData = null;
            Context context;

            @Override
            protected Void doInBackground(Object... params) {
                try {
                    context = (Context) params[0];
                    InputStream is = new ByteArrayInputStream(annotation.getBytes());
                    supData = semanticReasoner.loadResource(is);
                } catch (OWLOntologyCreationException e1) {
                    e1.printStackTrace();
                }
                return null;

            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                IRI supply = (IRI) supData.get("iri");
                if (supply != null) {
                    double score = semanticReasoner.calculateScore(supply, semanticReasoner.getDemand(), (String) supData.get("url"), userData);
                    SemanticData d = new SemanticData(annotation, score);
                    d.setIconUrl((String) supData.get("icon"));
                    d.setRefUrl((String) supData.get("url"));
                    mUrlToSemantic.put(url, d);
                    if (d.getRefUrl() != null)
                        InnerMetadataResolver.findInnerUrlMetadata(context, UriBeaconSemanticDiscoveryService.this, url, d.getRefUrl(), 0, 0);
                } else {
                    SemanticData d = new SemanticData(annotation, SemanticReasoner.UNDEFINED_SCORE);
                    d.setIconUrl((String) supData.get("icon"));
                    d.setRefUrl((String) supData.get("url"));
                    mUrlToSemantic.put(url, d);
                    if (d.getRefUrl() != null)
                        InnerMetadataResolver.findInnerUrlMetadata(context, UriBeaconSemanticDiscoveryService.this, url, d.getRefUrl(), 0, 0);
                }
                updateNotifications();
            }
        }.execute(this);

    }

    @Override
    public void onDemoUrlMetadataReceived(String url, MetadataResolver.UrlMetadata urlMetadata) {

    }

    @Override
    public void onUrlMetadataIconReceived() {
        updateNotifications();
    }

    @Override
    public void onMdnsUrlFound(String url) {
        if (!mUrlToUrlMetadata.containsKey(url)) {
            // Fabricate the device values so that we can show these ersatz beacons
            String mockAddress = generateMockBluetoothAddress(url.hashCode());
            int mockRssi = 0;
            int mockTxPower = 0;
            mUrlToUrlMetadata.put(url, null);
            mDeviceAddressToUrl.put(mockAddress, url);
            mRegionResolver.onUpdate(mockAddress, mockRssi, mockTxPower);
            MetadataResolver.findUrlMetadata(this, UriBeaconSemanticDiscoveryService.this, url, mockTxPower, mockRssi);
        }
    }

    private void startSearchingForUriBeacons() {
        ScanSettings settings = new ScanSettings.Builder()
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build();

        List<ScanFilter> filters = new ArrayList<>();

        ScanFilter filter = new ScanFilter.Builder()
                .setServiceData(UriBeacon.URI_SERVICE_UUID,
                        new byte[]{},
                        new byte[]{})
                .build();

        filters.add(filter);

        boolean started = getLeScanner().startScan(filters, settings, mScanCallback);
        Log.v(TAG, started ? "... scan started" : "... scan NOT started");
    }

    private void stopSearchingForUriBeacons() {
        getLeScanner().stopScan(mScanCallback);
    }

    private void handleFoundDevice(ScanResult scanResult) {
        long timeStamp = scanResult.getTimestampNanos();
        long now = TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis());
        if (now - timeStamp < TimeUnit.SECONDS.toNanos(TIMEOUT_FOR_OLD_BEACONS)) {
            UriBeacon uriBeacon = UriBeacon.parseFromBytes(scanResult.getScanRecord().getBytes());
            if (uriBeacon != null) {
                String address = scanResult.getDevice().getAddress();
                int rssi = scanResult.getRssi();
                int txPower = uriBeacon.getTxPowerLevel();
                String url = uriBeacon.getUriString();
                // If we haven't yet seen this url
                if (!mUrlToUrlMetadata.containsKey(url)) {
                    mUrlToUrlMetadata.put(url, null);
                    mDeviceAddressToUrl.put(address, url);
                    // Fetch the metadata for this url
                    MetadataResolver.findUrlMetadata(this, UriBeaconSemanticDiscoveryService.this, url, txPower, rssi);
                }
                // Update the ranging data
                mRegionResolver.onUpdate(address, rssi, txPower);
            }
        }
    }

    /**
     * Create a new set of notifications or update those existing
     */
    private void updateNotifications() {
        if (!mCanUpdateNotifications) {
            return;
        }

        mSortedDevices = getSortedBeaconsWithMetadata();

        // If no beacons have been found
        if (mSortedDevices.size() == 0) {
            // Remove all existing notifications
            mNotificationManager.cancelAll();
        } else if (mSortedDevices.size() == 1) {
            updateNearbyBeaconNotification(true, mDeviceAddressToUrl.get(mSortedDevices.get(0)),
                    NEAREST_BEACON_NOTIFICATION_ID);
        } else {
            // Create a summary notification for both beacon notifications.
            // Do this first so that we don't first show the individual notifications
            updateSummaryNotification();
            // Create or update a notification for second beacon
            updateNearbyBeaconNotification(false, mDeviceAddressToUrl.get(mSortedDevices.get(1)),
                    SECOND_NEAREST_BEACON_NOTIFICATION_ID);
            // Create or update a notification for first beacon. Needs to be added last to show up top
            updateNearbyBeaconNotification(false, mDeviceAddressToUrl.get(mSortedDevices.get(0)),
                    NEAREST_BEACON_NOTIFICATION_ID);

        }
    }

    private ArrayList<String> getSortedBeaconsWithMetadata() {
        ArrayList<String> unSorted = new ArrayList<>(mDeviceAddressToUrl.size());
        for (String key : mDeviceAddressToUrl.keySet()) {
            if (mUrlToUrlMetadata.get(mDeviceAddressToUrl.get(key)) != null) {
                unSorted.add(key);
            }
        }

        Collections.sort(unSorted, mSortBySemanticScoreComparator);
    /*// If there are scores in the metadata
    if (MetadataResolver.checkIfMetadataContainsSortingScores(mUrlToUrlMetadata.values())) {
      // Sort using those scores
      Collections.sort(unSorted, mSortByProxyServerScoreComparator);
    }
    // If there are not scores in the metadata
    else {
      // Sort using the region resolver regions
      Collections.sort(unSorted, mSortByRegionResolverRegionComparator);
    }*/
        return unSorted;
    }

    /**
     * Create or update a notification with the given id
     * for the beacon with the given address
     */
    private void updateNearbyBeaconNotification(boolean single, String url, int notificationId) {
        MetadataResolver.UrlMetadata urlMetadata = mUrlToUrlMetadata.get(url);
        if (urlMetadata == null) {
            return;
        }

        // Create an intent that will open the browser to the beacon's url
        // if the user taps on the notification
        PendingIntent pendingIntent = createNavigateToUrlPendingIntent(url);
        SemanticData sd = mUrlToSemantic.get(url);
        String title = urlMetadata.title;
        String description = urlMetadata.description;
        Bitmap icon = urlMetadata.icon;

        if (sd != null) {
            if (sd.getRefUrlMetadata() != null) {

                title = sd.getRefUrlMetadata().title;
                description = sd.getRefUrlMetadata().description;
                icon = sd.getRefUrlMetadata().icon;
            } else {
                String siteUrl = mUrlToUrlMetadata.get(url).siteUrl;
                title = siteUrl.substring(siteUrl.lastIndexOf("/") + 1);
                description = sd.getAnnotation();
            }

        } else if (urlMetadata != null) {
            title = mUrlToUrlMetadata.get(url).title;
            description = mUrlToUrlMetadata.get(url).description;

        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(icon)
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(NOTIFICATION_PRIORITY)
                .setContentIntent(pendingIntent);
        // For some reason if there is only one notification and you call setGroup
        // the notification doesn't show up on the N7 running kit kat
        if (!single) {
            builder = builder.setGroup(NOTIFICATION_GROUP_KEY);
        }
        Notification notification = builder.build();

        mNotificationManager.notify(notificationId, notification);
    }

    /**
     * Create or update the a single notification that is a collapsed version
     * of the top two beacon notifications
     */
    private void updateSummaryNotification() {
        int numNearbyBeacons = mSortedDevices.size();
        String contentTitle = String.valueOf(numNearbyBeacons) + " ";
        Resources resources = getResources();
        contentTitle += " " + resources.getQuantityString(R.plurals.numFoundBeacons, numNearbyBeacons, numNearbyBeacons);
        String contentText = getString(R.string.summary_notification_pull_down);
        PendingIntent pendingIntent = createReturnToAppPendingIntent();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Notification notification = builder.setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_notification)
                .setGroup(NOTIFICATION_GROUP_KEY)
                .setGroupSummary(true)
                .setPriority(NOTIFICATION_PRIORITY)
                .setContentIntent(pendingIntent)
                .build();

        // Create the big view for the notification (viewed by pulling down)
        RemoteViews remoteViews = updateSummaryNotificationRemoteViews();
        notification.bigContentView = remoteViews;

        mNotificationManager.notify(SUMMARY_NOTIFICATION_ID, notification);
    }

    /**
     * Create the big view for the summary notification
     */
    private RemoteViews updateSummaryNotificationRemoteViews() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.semantic_notification_big_view);

        // Fill in the data for the top two beacon views
        updateSummaryNotificationRemoteViewsFirstBeacon(mDeviceAddressToUrl.get(mSortedDevices.get(0)), remoteViews);
        updateSummaryNotificationRemoteViewsSecondBeacon(mDeviceAddressToUrl.get(mSortedDevices.get(1)), remoteViews);

        // Create a pending intent that will open the physical web app
        // TODO: Use a clickListener on the VIEW MORE button to do this
        PendingIntent pendingIntent = createReturnToAppPendingIntent();
        remoteViews.setOnClickPendingIntent(R.id.otherBeaconsLayout, pendingIntent);

        return remoteViews;
    }

    private void updateSummaryNotificationRemoteViewsFirstBeacon(String url, RemoteViews remoteViews) {
        MetadataResolver.UrlMetadata urlMetadata_firstBeacon = mUrlToUrlMetadata.get(url);

        SemanticData sd = mUrlToSemantic.get(url);
        if (sd != null) {
            if (sd.getRefUrlMetadata() != null) {
                remoteViews.setImageViewBitmap(R.id.icon_firstBeacon, sd.getRefUrlMetadata().icon);
                remoteViews.setTextViewText(R.id.title_firstBeacon, sd.getRefUrlMetadata().title);
                remoteViews.setTextViewText(R.id.url_firstBeacon, sd.getRefUrlMetadata().siteUrl);
                remoteViews.setTextViewText(R.id.description_firstBeacon, sd.getRefUrlMetadata().description);
            } else {
                String siteUrl = mUrlToUrlMetadata.get(url).siteUrl;
                String name = siteUrl.substring(siteUrl.lastIndexOf("/") + 1);
                remoteViews.setTextViewText(R.id.title_firstBeacon, name);
                remoteViews.setTextViewText(R.id.description_firstBeacon, sd.getAnnotation());
            }
            if (sd.getPenalty() != SemanticReasoner.UNDEFINED_SCORE) {
               // remoteViews.setTextViewText(R.id.score_firstBeacon, "Score: " + String.valueOf(Math.round(sd.getPenalty())) + "%");
            } else {
               // remoteViews.setTextViewText(R.id.score_firstBeacon, "Undefined");
            }
        } else if (urlMetadata_firstBeacon != null) {
            String title = mUrlToUrlMetadata.get(url).title;
            String description = mUrlToUrlMetadata.get(url).description;
            Bitmap icon = mUrlToUrlMetadata.get(url).icon;
            {
                remoteViews.setImageViewBitmap(R.id.icon_firstBeacon, icon);
                remoteViews.setTextViewText(R.id.title_firstBeacon, title);
               // remoteViews.setTextViewText(R.id.url_firstBeacon, url);
               // remoteViews.setTextViewText(R.id.description_firstBeacon, description);
               //  remoteViews.setTextViewText(R.id.score_firstBeacon, "Undefined");

            }
            // Recolor notifications to have light text for non-Lollipop devices
            if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
                remoteViews.setTextColor(R.id.title_firstBeacon, NON_LOLLIPOP_NOTIFICATION_TITLE_COLOR);
                remoteViews.setTextColor(R.id.url_firstBeacon, NON_LOLLIPOP_NOTIFICATION_URL_COLOR);
                remoteViews.setTextColor(R.id.description_firstBeacon, NON_LOLLIPOP_NOTIFICATION_SNIPPET_COLOR);
            }

            // Create an intent that will open the browser to the beacon's url
            // if the user taps the notification
            PendingIntent pendingIntent = createNavigateToUrlPendingIntent(url);
            remoteViews.setOnClickPendingIntent(R.id.first_beacon_main_layout, pendingIntent);
            remoteViews.setViewVisibility(R.id.firstBeaconLayout, View.VISIBLE);
        } else {
            remoteViews.setViewVisibility(R.id.firstBeaconLayout, View.GONE);
        }
    }

    private void updateSummaryNotificationRemoteViewsSecondBeacon(String url, RemoteViews remoteViews) {
        MetadataResolver.UrlMetadata urlMetadata_secondBeacon = mUrlToUrlMetadata.get(url);
        SemanticData sd = mUrlToSemantic.get(url);
        if (sd != null) {
            if (sd.getRefUrlMetadata() != null) {
                remoteViews.setImageViewBitmap(R.id.icon_secondBeacon, sd.getRefUrlMetadata().icon);
                remoteViews.setTextViewText(R.id.title_secondBeacon, sd.getRefUrlMetadata().title);
               // remoteViews.setTextViewText(R.id.url_secondBeacon, sd.getRefUrlMetadata().siteUrl);
               // remoteViews.setTextViewText(R.id.description_secondBeacon, sd.getRefUrlMetadata().description);
            } else {
                String siteUrl = mUrlToUrlMetadata.get(url).siteUrl;
                String name = siteUrl.substring(siteUrl.lastIndexOf("/") + 1);
                remoteViews.setTextViewText(R.id.title_secondBeacon, name);
               // remoteViews.setTextViewText(R.id.description_secondBeacon, sd.getAnnotation());
            }
            if (sd.getPenalty() != SemanticReasoner.UNDEFINED_SCORE) {
                //remoteViews.setTextViewText(R.id.score_secondBeacon, "Score: " + String.valueOf(Math.round(sd.getPenalty())) + "%");
            } else {
                //remoteViews.setTextViewText(R.id.score_secondBeacon, "Undefined");
            }
        } else if (urlMetadata_secondBeacon != null) {
            String title = mUrlToUrlMetadata.get(url).title;
            String description = mUrlToUrlMetadata.get(url).description;
            Bitmap icon = mUrlToUrlMetadata.get(url).icon;
            {
                remoteViews.setImageViewBitmap(R.id.icon_secondBeacon, icon);
                remoteViews.setTextViewText(R.id.title_secondBeacon, title);
              //  remoteViews.setTextViewText(R.id.url_secondBeacon, url);
               // remoteViews.setTextViewText(R.id.description_secondBeacon, description);
                //remoteViews.setTextViewText(R.id.score_secondBeacon, "Undefined");
            }

            // Recolor notifications to have light text for non-Lollipop devices
            if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
                remoteViews.setTextColor(R.id.title_secondBeacon, NON_LOLLIPOP_NOTIFICATION_TITLE_COLOR);
                remoteViews.setTextColor(R.id.url_secondBeacon, NON_LOLLIPOP_NOTIFICATION_URL_COLOR);
                remoteViews.setTextColor(R.id.description_secondBeacon, NON_LOLLIPOP_NOTIFICATION_SNIPPET_COLOR);
            }

            // Create an intent that will open the browser to the beacon's url
            // if the user taps the notification
            PendingIntent pendingIntent = createNavigateToUrlPendingIntent(url);
            remoteViews.setOnClickPendingIntent(R.id.second_beacon_main_layout, pendingIntent);
            remoteViews.setViewVisibility(R.id.secondBeaconLayout, View.VISIBLE);
        } else {
            remoteViews.setViewVisibility(R.id.secondBeaconLayout, View.GONE);
        }
    }

    private PendingIntent createReturnToAppPendingIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        int requestID = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestID, intent, 0);
        return pendingIntent;
    }

    private PendingIntent createNavigateToUrlPendingIntent(String url) {
        String urlToNavigateTo = url;
        // If this url has metadata
        if (mUrlToUrlMetadata.get(url) != null) {
            String siteUrl = mUrlToUrlMetadata.get(url).siteUrl;
            // If this metadata has a siteUrl
            if (siteUrl != null) {
                urlToNavigateTo = siteUrl;
            }
        }
        if (mUrlToSemantic.get(url) != null) {
            if (mUrlToSemantic.get(url).getRefUrl() != null) {
                urlToNavigateTo = mUrlToSemantic.get(url).getRefUrl();
            }
        }
        if (!URLUtil.isNetworkUrl(urlToNavigateTo)) {
            urlToNavigateTo = "http://" + urlToNavigateTo;
        }
        urlToNavigateTo = MetadataResolver.createUrlProxyGoLink(urlToNavigateTo);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(urlToNavigateTo));
        int requestID = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestID, intent, 0);
        return pendingIntent;
    }

    @Override
    public void onInnerUrlMetadataReceived(String annUrl, String url, InnerMetadataResolver.UrlMetadata urlMetadata) {
        mUrlToSemantic.get(annUrl).setRefUrlMetadata(urlMetadata);
        updateNotifications();
    }

    @Override
    public void onInnerUrlMetadataIconReceived(String annUrl) {
        updateNotifications();
    }

    /**
     * This is the class that listens for screen on/off events
     */
    private class ScreenBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isScreenOn = Intent.ACTION_SCREEN_ON.equals(intent.getAction());
            initializeLists();
            mNotificationManager.cancelAll();
            if (isScreenOn) {
                mCanUpdateNotifications = false;
                mHandler.postDelayed(mNotificationUpdateGateTimeout, NOTIFICATION_UPDATE_GATE_DURATION);
                startSearchingForUriBeacons();
                //mMdnsUrlDiscoverer.startScanning();
            } else {
                mHandler.removeCallbacks(mNotificationUpdateGateTimeout);
                stopSearchingForUriBeacons();
                //mMdnsUrlDiscoverer.stopScanning();
            }
        }
    }
}
