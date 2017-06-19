package org.physical_web.physicalweb;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class BeaconWebActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);

        // MainActivity からのデータを受け取る
        Intent intent = getIntent();
        String data = intent.getStringExtra("URL");

        WebView  myWebView = (WebView)findViewById(R.id.webView1);
        //標準ブラウザをキャンセル
        myWebView.setWebViewClient(new WebViewClient());
        //jacascriptを許可する
        myWebView.getSettings().setJavaScriptEnabled(true);
        //アプリ起動時に読み込むURL
        myWebView.loadUrl(data);
    }
}
