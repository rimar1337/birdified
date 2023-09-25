package com.birdified.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.CookieManager;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

    private WebView mWebView;

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = findViewById(R.id.activity_main_webview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAppCacheEnabled(true);
        mWebView.setWebViewClient(new MyWebViewClient());

        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);

        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        String csp = "default-src 'self' https://bsky.app; style-src 'self' https://gist.githubusercontent.com";
        mWebView.loadUrl("javascript:window.csp='" + csp + "'");

        android.util.Log.d("MyApp", "Running injection script");

        // Load the remote web page
        mWebView.loadUrl("https://bsky.app");

        // Inject local CSS into the remote web page when it's finished loading
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                String js = "fetch('https://gist.githubusercontent.com/rimar1337/cf96ee982afe28854281193728240c41/raw/style.css')" +
                        ".then(response => response.text())" +
                        ".then(css => {" +
                        "    const style = document.createElement('style');" +
                        "    style.innerHTML = css;" +
                        "    document.head.appendChild(style);" +
                        "    console.log('MyApp real wowza js CSS injected!');" +
                        "});";
                mWebView.evaluateJavascript(js, null);
                android.util.Log.d("MyApp", "Injected CSS using JavaScript");
                super.onPageFinished(view, url);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
