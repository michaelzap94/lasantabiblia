package com.zapatatech.santabiblia;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class Maps extends AppCompatActivity {

    public class MyWebClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            view.loadUrl("javascript:(function() { " +
                    "document.getElementById('PFmainHeader').style.display='none';})()");
        }
    }

    private WebView mapsWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mapsWebView = findViewById(R.id.maps_webview);

        WebSettings webSettings = mapsWebView.getSettings();
        final MyWebClient myWebViewClient = new MyWebClient();
        mapsWebView.setWebViewClient(myWebViewClient);
        mapsWebView.loadUrl("https://www.churchofjesuschrist.org/study/scriptures/bible-maps/overview?lang=spa");
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
    }
}
