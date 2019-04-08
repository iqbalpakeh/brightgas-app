package com.pertamina.brightgas.retrofit.listaddress;

import android.content.Context;
import android.util.Log;

import com.pertamina.brightgas.AppLocal;
import com.pertamina.brightgas.retrofit.ApiClient;
import com.pertamina.brightgas.retrofit.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListAddressClient {

    private static final String TAG = "api_list_address";

    private ListAddressInterface mInterface;
    private ListAddressResponse mResponse;
    private Context mContext;

    public ListAddressClient(Context context, ListAddressInterface anInterface) {
        mInterface = anInterface;
        mContext = context;
    }

    public void listAddress() {
        ApiInterface client = ApiClient.getClient().create(ApiInterface.class);
        Call<ListAddressResponse> call = client.listAddress(AppLocal.getToken(mContext));
        call.enqueue(new Callback<ListAddressResponse>() {
            @Override
            public void onResponse(Call<ListAddressResponse> call, Response<ListAddressResponse> response) {
                Log.d(TAG, "RETROFIT RESPONSE OK");
                Log.d(TAG, "HTTP RAW: " + response.raw());
                Log.d(TAG, "HTTP CODE: " + response.code());
                Log.d(TAG, "HTTP HEADERS: " + response.headers());
                if (response.isSuccessful()) {
                    ListAddressResponse listAddressResponse = response.body();
                    mInterface.retrofitListAddress(listAddressResponse);
                } else {
                    Log.d(TAG, "HTTP ERROR BODY: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ListAddressResponse> call, Throwable t) {
                Log.d(TAG, "RETROFIT RESPONSE ERROR");
            }
        });
    }

}
