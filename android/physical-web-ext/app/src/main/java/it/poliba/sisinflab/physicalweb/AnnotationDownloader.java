package it.poliba.sisinflab.physicalweb;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by giorgio on 11/02/15.
 */
public class AnnotationDownloader {

    public interface AnnotationDownloaderCallback {
        public void onAnnotationDownloaded(String url, String annotation);
    }

    public static void downloadAnnotation(Context context, AnnotationDownloaderCallback callback, String beaconUrl, String longUrl) {
        new AnnotationDownloadTask().execute(context, callback, beaconUrl, longUrl);
    }

    private static class AnnotationDownloadTask extends AsyncTask<Object, Void, String[]> {

        Context context;
        AnnotationDownloaderCallback callback;
        String beaconUrl, longUrl;

        @Override
        protected String[] doInBackground(Object... params) {
            context = (Context) params[0];
            callback = (AnnotationDownloaderCallback) params[1];
            beaconUrl = (String) params[2];
            longUrl = (String) params[3];
            String annotation = null;
            try {
                /*if (isShortUrl(beaconUrl)) {
                    String longUrl = lengthenUrl(beaconUrl);
                    if(longUrl.endsWith(".owl")) {
                        annotation = downloadAnnotation(longUrl);
                        return new String[]{beaconUrl, annotation};
                    }else
                        return null;
                }else{*/
                if (longUrl.endsWith(".owl")) {
                    annotation = downloadAnnotation(longUrl);
                    return new String[]{beaconUrl, annotation};
                } else
                    return null;
                //}
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] s) {
            super.onPostExecute(s);
            if (s != null)
                callback.onAnnotationDownloaded(s[0], s[1]);
        }
    }

    public static boolean isShortUrl(String url) {
        return url.startsWith("http://goo.gl/") || url.startsWith("https://goo.gl/");
    }

    public static String downloadAnnotation(String beaconUrl) throws IOException {
        URL url = new URL(beaconUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine, annotation = "";
        while ((inputLine = in.readLine()) != null) {
            annotation += inputLine;
            annotation += System.lineSeparator();
        }
        in.close();
        return annotation;
    }

    public static String lengthenUrl(String shortUrl) throws IOException {
        URL url = new URL(shortUrl);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setInstanceFollowRedirects(false);
        String longUrl = httpURLConnection.getHeaderField("location");
        return (longUrl != null) ? longUrl : shortUrl;
    }

    public static String readFile(String path) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            String inputLine, annotation = "";
            while ((inputLine = in.readLine()) != null) {
                annotation += inputLine;
            }
            in.close();
            return annotation;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
