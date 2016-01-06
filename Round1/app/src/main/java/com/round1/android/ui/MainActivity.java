package com.round1.android.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.round1.android.R;
import com.round1.android.api.ApiHelper;
import com.round1.android.api.WebViewsParser;
import com.round1.android.model.WebSiteModel;
import com.round1.android.utils.NetworkUtils;
import com.round1.android.utils.UIUtils;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {

    private ListAdapter adapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(this);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ListAdapter(this, onItemClickListener);
        recyclerView.setAdapter(adapter);

        getDataJson();
    }

    private void getDataJson(){
        if (!NetworkUtils.isInternetAvailable(getBaseContext())){
            UIUtils.showToast(getBaseContext(), R.string.error_internet_connection);
            return;
        }

        UIUtils.showProgressDialog(progressDialog, false);
        new ApiHelper().getData(getDataCallback);
    }

    private ListAdapter.OnItemClickListener onItemClickListener = new ListAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view) {
            final WebSiteModel data = (WebSiteModel) view.getTag(R.id.data_id);

            if (data == null){
                return;
            }

            final Intent intent = new Intent(getBaseContext(), DetailsActivity.class);
            intent.putExtra("data", data);
            startActivity(intent);
        }
    };

    private Callback<Response> getDataCallback = new Callback<Response>() {
        @Override
        public void success(Response response, Response response2) {
            new WebViewsParser(getApplicationContext(), onJsonParsedCallback).execute(response);
        }

        @Override
        public void failure(RetrofitError error) {
            Log.e("getDataCallback", "Unable to get json");
            UIUtils.hideProgressDialog(progressDialog);
            UIUtils.showToast(getBaseContext(), R.string.error_connection_to_server);
        }
    };

    private WebViewsParser.WebViewsParserCallback onJsonParsedCallback = new WebViewsParser.WebViewsParserCallback() {
        @Override
        public void onSuccess(final ArrayList<WebSiteModel> dataList) {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.swapData(dataList);
                    UIUtils.hideProgressDialog(progressDialog);
                }
            });
        }

        @Override
        public void onError() {
            UIUtils.hideProgressDialog(progressDialog);
            UIUtils.showToast(getBaseContext(), R.string.error_connection_to_server);
        }
    };
}
