package com.pertamina.brightgas.retrofit.customerprofile;

import android.content.Context;
import android.util.Log;

import com.pertamina.brightgas.AppLocal;
import com.pertamina.brightgas.retrofit.ApiClient;
import com.pertamina.brightgas.retrofit.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerProfileClient {

    private static final String TAG = "api_show_profile";

    private CustomerProfileInterface mCustomerProfileInterface;
    private Context mContext;

    public CustomerProfileClient(Context context, CustomerProfileInterface customerProfileInterface) {
        mContext = context;
        mCustomerProfileInterface = customerProfileInterface;
    }

    public void showProfile() {
        ApiInterface client = ApiClient.getClient().create(ApiInterface.class);
        Call<CustomerProfileResponse> call = client.customerProfile(AppLocal.getToken(mContext));
        call.enqueue(new Callback<CustomerProfileResponse>() {
            @Override
            public void onResponse(Call<CustomerProfileResponse> call, Response<CustomerProfileResponse> response) {
                Log.d(TAG, "RETROFIT RESPONSE OK");
                Log.d(TAG, "HTTP RAW: " + response.raw());
                Log.d(TAG, "HTTP CODE: " + response.code());
                Log.d(TAG, "HTTP HEADERS: " + response.headers());
                if (response.isSuccessful()) {
                    CustomerProfileResponse customerProfileResponse = response.body();
                    mCustomerProfileInterface.retrofitShowProfile(customerProfileResponse);
                } else {
                    Log.d(TAG, "HTTP ERROR BODY: " + response.errorBody());
                    mCustomerProfileInterface.retrofitShowProfile(null);
                }
            }

            @Override
            public void onFailure(Call<CustomerProfileResponse> call, Throwable t) {
                Log.d(TAG, "RETROFIT RESPONSE ERROR");
            }
        });
    }

}
