package com.pertamina.brightgas.retrofit.listproduct;

import android.content.Context;
import android.util.Log;

import com.pertamina.brightgas.AppLocal;
import com.pertamina.brightgas.retrofit.ApiClient;
import com.pertamina.brightgas.retrofit.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListProductClient {

    private static final String TAG = "api_list_product";

    private ListProductInterface mInterface;
    private Context mContext;

    public ListProductClient(Context context, ListProductInterface anInterface) {
        mContext = context;
        mInterface = anInterface;
    }

    public void listProduct() {
        ApiInterface client = ApiClient.getClient().create(ApiInterface.class);
        Call<ListProductResponse> call = client.listProduct(AppLocal.getToken(mContext));
        call.enqueue(new Callback<ListProductResponse>() {
            @Override
            public void onResponse(Call<ListProductResponse> call, Response<ListProductResponse> response) {
                Log.d(TAG, "RETROFIT RESPONSE OK");
                Log.d(TAG, "HTTP RAW: " + response.raw());
                Log.d(TAG, "HTTP CODE: " + response.code());
                Log.d(TAG, "HTTP HEADERS: " + response.headers());
                if (response.isSuccessful()) {
                    ListProductResponse listProductResponse = response.body();
                    mInterface.retrofitListProduct(listProductResponse);
                } else {
                    Log.d(TAG, "HTTP ERROR BODY: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ListProductResponse> call, Throwable t) {
                Log.d(TAG, "RETROFIT RESPONSE ERROR");
            }
        });
    }

}
