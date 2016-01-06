package com.round1.android.api;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.round1.android.model.WebSiteModel;
import com.round1.android.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.client.Response;

public class WebViewsParser extends AsyncTask<Response, Void, ArrayList<WebSiteModel>> {

    private static Map<String, String> replacementMap;

    static {
        replacementMap = new HashMap<>();
        replacementMap.put("{userId}", "276");
        replacementMap.put("{appSecretKey}", "gvx32RFZLNGhmzYrfDCkb9jypTPa8Q");
        replacementMap.put("{currencyCode}", "USD");
        replacementMap.put("{offerId}", "10736598");
        replacementMap.put("{selectedVouchers}", "[]");
    }

    private WebViewsParserCallback webViewsParserCallback;

    public WebViewsParser(WebViewsParserCallback webViewsParserCallback) {
        this.webViewsParserCallback = webViewsParserCallback;
    }

    @Override
    protected ArrayList<WebSiteModel> doInBackground(Response... responses) {

        try{
            final String responseString = NetworkUtils.readResponse(responses[0]);

            final JSONObject object = new JSONObject(responseString);
            final JSONArray namesArray = object.names();

            final ArrayList<WebSiteModel> models = new ArrayList<>();
            for (int i = 0; i < namesArray.length(); i++) {
                final String objectName = namesArray.optString(i);

                if (!TextUtils.isEmpty(objectName)) {
                    final WebSiteModel model = parseWebView(object, objectName);
                    if(model != null) models.add(model);
                }
            }

           return models;

        } catch (Exception exc) {
            Log.e("WebViewParser", "Unable to parse webviews", exc);
           return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<WebSiteModel> webSiteModels) {
        super.onPostExecute(webSiteModels);

        if (webViewsParserCallback == null){
            return;
        }

        if (webSiteModels == null){
            webViewsParserCallback.onError();
        }else {
            webViewsParserCallback.onSuccess(webSiteModels);
        }
    }

    private WebSiteModel parseWebView(JSONObject object, String name) throws JSONException {
        final JSONObject foundObject = object.optJSONObject(name);

        if (foundObject == null) return null;

        final WebSiteModel model = new WebSiteModel();
        model.name = name;
        model.url = getUrlWithReplacements(foundObject.optString("url"));
        model.cached = foundObject.optBoolean("cache");

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

    public interface WebViewsParserCallback {
        void onSuccess(ArrayList<WebSiteModel> models);
        void onError();
    }
}
