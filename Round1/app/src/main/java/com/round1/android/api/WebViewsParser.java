package com.round1.android.api;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.round1.android.Config;
import com.round1.android.model.WebSiteModel;
import com.round1.android.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.client.Response;

public class WebViewsParser extends AsyncTask<Response, Void, Void> {

    private static Map<String, String> replacementMap;

    static {
        replacementMap = new HashMap<>();
        replacementMap.put("{userId}", "276");
        replacementMap.put("{appSecretKey}", "gvx32RFZLNGhmzYrfDCkb9jypTPa8Q");
        replacementMap.put("{currencyCode}", "USD");
        replacementMap.put("{offerId}", "10736598");
        replacementMap.put("{selectedVouchers}", "[]");
    }

    private Context context;
    private WebViewsParserCallback webViewsParserCallback;

    public WebViewsParser(Context context, WebViewsParserCallback webViewsParserCallback) {
        if (context == null) {
            Log.e("WebViewParser", "Unable to start parsing. Context is null");
            return;
        }

        this.context = context.getApplicationContext();
        this.webViewsParserCallback = webViewsParserCallback;
    }

    @Override
    protected Void doInBackground(Response... responses) {

        try {
            final String responseString = NetworkUtils.readResponse(responses[0]);

            final JSONObject object = new JSONObject(responseString);
            final JSONArray namesArray = object.names();

            final ArrayList<WebSiteModel> models = new ArrayList<>();
            for (int i = 0; i < namesArray.length(); i++) {
                final String objectName = namesArray.optString(i);

                if (!TextUtils.isEmpty(objectName)) {
                    final WebSiteModel model = parseWebView(object, objectName);
                    if (model != null) models.add(model);
                }
            }

            if (webViewsParserCallback != null) webViewsParserCallback.onSuccess(models);

        } catch (Exception exc) {
            Log.e("WebViewParser", "Unable to parse webviews", exc);
            if (webViewsParserCallback != null) webViewsParserCallback.onError();
        }

        return null;
    }

    private WebSiteModel parseWebView(JSONObject object, String name) throws JSONException {
        final JSONObject foundObject = object.optJSONObject(name);

        if (foundObject == null) return null;

        final WebSiteModel model = new WebSiteModel();
        model.name = name;
        model.url = getUrlWithReplacements(foundObject.optString("url"));
        model.cached = foundObject.optBoolean("cache");

        if (!model.cached) return model;

        final String cachedFileName = getCachedFileName(model.url) + ".html";
        final File cachedFile = new File(context.getCacheDir().getAbsolutePath() + File.separator + cachedFileName);
        if (cachedFile.exists()) {
            model.cachedPagePath = cachedFile.getAbsolutePath();
            return model;
        }

        model.cachedPagePath = cacheWebPageByURL(model.url, cachedFile);

        return model;
    }

    private String getUrlWithReplacements(String baseUrl) {
        if (TextUtils.isEmpty(baseUrl)) return baseUrl;

        String resultUrl = baseUrl;
        for (String replacement : replacementMap.keySet()) {
            resultUrl = resultUrl.replace(replacement, replacementMap.get(replacement));
        }

        return resultUrl;
    }

    private String getCachedFileName(String url) {
        return url.replace(Config.DOMAIN_URL, "").replaceAll("[^A-Za-z0-9.]", "_");
    }

    private String cacheWebPageByURL(String urlString, File file) {
        try {
            final URL url = new URL(urlString);

            final long startTime = System.currentTimeMillis();
            Log.d("WebViewDownload", "Download start by url: " + url);

            /* Open a connection to that URL. */
            final URLConnection ucon = url.openConnection();
            ucon.setConnectTimeout(25000);

			/*
             * Define InputStreams to read from the URLConnection.
			 */
            final InputStream is = ucon.getInputStream();
            final BufferedInputStream bis = new BufferedInputStream(is);
            final FileOutputStream fos = new FileOutputStream(file);

			/*
             * Read bytes to the Buffer until there is nothing more to read(-1).
			 */
            final byte[] data = new byte[4096];
            int current;
            while ((current = bis.read(data)) != -1) {
                fos.write(data, 0, current);
            }

            is.close();
            bis.close();
            fos.close();
            Log.d("WebViewDownload", "downloaded file name:" + file.getPath());
            Log.d("WebViewDownload", "download ready in" + ((System.currentTimeMillis() - startTime) / 1000) + " sec");
        } catch (IOException ex) {
            Log.e("WebViewDownload", ex.getMessage());
            return null;
        }

        return file.getAbsolutePath();
    }

    public interface WebViewsParserCallback {
        void onSuccess(ArrayList<WebSiteModel> models);

        void onError();
    }
}
