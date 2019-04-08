package com.pertamina.brightgas.retrofit.login;

import android.content.Context;
import android.util.Log;

import com.pertamina.brightgas.AppLocal;
import com.pertamina.brightgas.retrofit.ApiClient;
import com.pertamina.brightgas.retrofit.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginClient {

    private static final String TAG = "login_client";

    private LoginInterface mLoginInterface;
    private Context mContext;

    public LoginClient(Context context, LoginInterface loginInterface) {
        mContext = context;
        mLoginInterface = loginInterface;
    }

    public void login(LoginRequest loginRequest) {
        ApiInterface client = ApiClient.getClient().create(ApiInterface.class);
        Call<LoginResponse> call = client.login(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                Log.d(TAG, "RETROFIT RESPONSE OK");
                Log.d(TAG, "HTTP RAW: " + response.raw());
                Log.d(TAG, "HTTP CODE: " + response.code());
                Log.d(TAG, "HTTP HEADERS: " + response.headers());

                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    AppLocal.storeToken(mContext, loginResponse.getToken());
                    Log.d(TAG, "token:" + AppLocal.getToken(mContext));
                    mLoginInterface.retrofitLogin(loginResponse);
                } else {
                    Log.d(TAG, "HTTP ERROR BODY: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.d(TAG, "RETROFIT RESPONSE ERROR");
            }
        });
    }
}
