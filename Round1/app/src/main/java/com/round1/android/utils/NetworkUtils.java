package com.round1.android.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import retrofit.client.Response;

public class NetworkUtils {

    public static boolean isInternetAvailable(Context context) {
        final ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo info = manager.getActiveNetworkInfo();

        return info != null && info.isConnected() && !info.isRoaming();
    }

    public static String readResponse(Response response) throws Exception {
        BufferedReader bufferedReader;
        final StringBuilder stringBuilder = new StringBuilder();
        bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }

        bufferedReader.close();

        return stringBuilder.toString();
    }
}
