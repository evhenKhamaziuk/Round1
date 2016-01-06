package com.round1.android.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.round1.android.R;
import com.round1.android.model.WebSiteModel;
import com.round1.android.utils.NetworkUtils;
import com.round1.android.utils.Utils;

public class DetailsActivity extends AppCompatActivity{

    private WebView webView;
    private ProgressBar progressBar;
    private boolean cached;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        final WebSiteModel data = getIntent().getParcelableExtra("data");

        if (data == null){
            return;
        }

        cached = data.cached;

        final TextView tvName = (TextView) findViewById(R.id.details_tv_name);
        tvName.setText(data.name);

        progressBar = (ProgressBar) findViewById(R.id.details_progress);
        webView = (WebView) findViewById(R.id.details_web_view);

        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setAllowFileAccess(true);
        webView.setWebChromeClient(new ChromeClient());
        webView.setWebViewClient(new WebViewClient());

        if (!NetworkUtils.isInternetAvailable(this)) { // loading offline
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        if (cached){
            webView.loadData(Utils.getStringFromFile(data.cachedPagePath), "text/html", "utf-8");
        }else {
            webView.loadUrl(data.url);
        }
    }

    public class WebViewClient extends android.webkit.WebViewClient {

        @Override
        public void onReceivedError(WebView view, int errorCode,  String description, String failingUrl) {
            webView.setVisibility(View.GONE);

            TextView tvError = (TextView) findViewById(R.id.details_tv_error);
            tvError.setVisibility(View.VISIBLE);
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //Override url redirect if required
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (cached){
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (cached){
                return;
            }
            progressBar.setVisibility(View.GONE);
        }
    }

    private class ChromeClient extends WebChromeClient {

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return super.onJsAlert(view, url, message, result);
        }
    }
}
