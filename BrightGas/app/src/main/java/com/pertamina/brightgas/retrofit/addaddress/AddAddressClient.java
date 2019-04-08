package com.pertamina.brightgas.retrofit.addaddress;

import android.content.Context;
import android.util.Log;

import com.pertamina.brightgas.AppLocal;
import com.pertamina.brightgas.retrofit.ApiClient;
import com.pertamina.brightgas.retrofit.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAddressClient {

    private static final String TAG = "api_add_address";

    private AddAddressInterface mInterface;
    private Context mContext;

    public AddAddressClient(Context context, AddAddressInterface anInterface) {
        mInterface = anInterface;
        mContext = context;
    }

    public void addAddress(AddAddressRequest request) {
        ApiInterface client = ApiClient.getClient().create(ApiInterface.class);
        Call<AddAddressResponse> call = client.addAddress(
                AppLocal.getToken(mContext),
                request
        );
        call.enqueue(new Callback<AddAddressResponse>() {
            @Override
            public void onResponse(Call<AddAddressResponse> call, Response<AddAddressResponse> response) {

                Log.d(TAG, "RETROFIT RESPONSE OK");
                Log.d(TAG, "HTTP RAW: " + response.raw());
                Log.d(TAG, "HTTP CODE: " + response.code());
                Log.d(TAG, "HTTP HEADERS: " + response.headers());

                if (response.isSuccessful()) {
                    mInterface.retrofitAddAddress(true);
                } else {
                    Log.d(TAG, "HTTP ERROR BODY: " + response.errorBody());
                    mInterface.retrofitAddAddress(false);
                }
            }

            @Override
            public void onFailure(Call<AddAddressResponse> call, Throwable t) {
                mInterface.retrofitAddAddress(false);
            }
        });
    }

}
