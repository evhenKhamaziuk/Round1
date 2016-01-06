package com.round1.android.api;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;

public interface ApiInterface {

    @GET("/en/1/android/index.json")
    void getOrder(Callback<Response> callback);
}
