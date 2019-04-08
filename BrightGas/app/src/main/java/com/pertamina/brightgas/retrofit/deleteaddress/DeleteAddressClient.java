package com.pertamina.brightgas.retrofit.deleteaddress;

import android.content.Context;
import android.util.Log;

import com.pertamina.brightgas.AppLocal;
import com.pertamina.brightgas.retrofit.ApiClient;
import com.pertamina.brightgas.retrofit.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeleteAddressClient {

    private static final String TAG = "api_del_address";

    private DeleteAddressInterface mInterface;
    private DeleteAddressResponse mResponse;
    private Context mContext;

    public DeleteAddressClient(Context context, DeleteAddressInterface anInterface) {
        this.mContext = context;
        this.mInterface = anInterface;
    }

    public void deleteAddress(String id, final int position) {
        Log.d(TAG, "id: " + id);
        ApiInterface client = ApiClient.getClient().create(ApiInterface.class);
        Call<DeleteAddressResponse> call = client.deleteAddress(
                AppLocal.getToken(mContext),
                id
        );
        call.enqueue(new Callback<DeleteAddressResponse>() {
            @Override
            public void onResponse(Call<DeleteAddressResponse> call, Response<DeleteAddressResponse> response) {

                Log.d(TAG, "RETROFIT RESPONSE OK");
                Log.d(TAG, "HTTP RAW: " + response.raw());
                Log.d(TAG, "HTTP CODE: " + response.code());
                Log.d(TAG, "HTTP HEADERS: " + response.headers());

                if (response.isSuccessful()) {
                    DeleteAddressResponse deleteAddressResponse = response.body();
                    mInterface.retrofitDeleteAddress(true, position);
                } else {
                    Log.d(TAG, "HTTP ERROR BODY: " + response.errorBody());
                    mInterface.retrofitDeleteAddress(false, position);
                }
            }

            @Override
            public void onFailure(Call<DeleteAddressResponse> call, Throwable t) {
                mInterface.retrofitDeleteAddress(false, position);
            }
        });
    }
}
