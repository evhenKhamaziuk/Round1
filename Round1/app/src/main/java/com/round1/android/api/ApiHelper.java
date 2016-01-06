package com.round1.android.api;

import com.round1.android.Config;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.Response;

public class ApiHelper {

    private ApiInterface apiInterface;

    public ApiHelper() {

        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Config.DOMAIN_URL)
                .build();

        apiInterface = restAdapter.create(ApiInterface.class);
    }

    public void getData(Callback<Response> callback){
        apiInterface.getOrder(callback);
    }
}
