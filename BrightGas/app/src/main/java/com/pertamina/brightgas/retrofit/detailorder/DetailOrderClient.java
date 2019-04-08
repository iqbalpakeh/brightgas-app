package com.pertamina.brightgas.retrofit.detailorder;

import android.content.Context;
import android.util.Log;

import com.pertamina.brightgas.AppLocal;
import com.pertamina.brightgas.retrofit.ApiClient;
import com.pertamina.brightgas.retrofit.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailOrderClient {

    private static final String TAG = "api_detail_order";

    private DetailOrderInterface mInterface;
    private Context mContext;

    public DetailOrderClient(Context context, DetailOrderInterface anInterface) {
        this.mContext = context;
        this.mInterface = anInterface;
    }

    public void detailOrder(String id) {
        ApiInterface client = ApiClient.getClient().create(ApiInterface.class);
        Call<DetailOrderResponse> call = client.showDetailOder(AppLocal.getToken(mContext), id);
        call.enqueue(new Callback<DetailOrderResponse>() {
            @Override
            public void onResponse(Call<DetailOrderResponse> call, Response<DetailOrderResponse> response) {
                Log.d(TAG, "RETROFIT RESPONSE OK");
                Log.d(TAG, "HTTP RAW: " + response.raw());
                Log.d(TAG, "HTTP CODE: " + response.code());
                Log.d(TAG, "HTTP HEADERS: " + response.headers());
                if (response.isSuccessful()) {
                    DetailOrderResponse detailOrderResponse = response.body();
                    mInterface.retrofitDetailOrder(detailOrderResponse);
                } else {
                    Log.d(TAG, "HTTP ERROR BODY: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<DetailOrderResponse> call, Throwable t) {
                Log.d(TAG, "RETROFIT RESPONSE ERROR");
            }
        });
    }

}
