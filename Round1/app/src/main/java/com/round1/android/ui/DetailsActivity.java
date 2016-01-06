package com.round1.android.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.round1.android.R;
import com.round1.android.model.WebSiteModel;
import com.round1.android.utils.NetworkUtils;

public class DetailsActivity extends AppCompatActivity{

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        final WebSiteModel data = getIntent().getParcelableExtra("data");

        if (data == null){
            return;
        }

        final TextView tvName = (TextView) findViewById(R.id.details_tv_name);
        tvName.setText(data.name);

        webView = (WebView) findViewById(R.id.details_web_view);

        webView.getSettings().setAppCachePath(getCacheDir().getPath());
        webView.getSettings().setAppCacheEnabled(data.cached);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setAllowFileAccess(true);
        webView.setWebChromeClient(new ChromeClient());
        webView.setWebViewClient(new WebViewClient());

        if (!NetworkUtils.isInternetAvailable(this)) { // loading offline
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        webView.loadUrl(data.url);
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
    }

    private class ChromeClient extends WebChromeClient {

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return super.onJsAlert(view, url, message, result);
        }
    }
}
