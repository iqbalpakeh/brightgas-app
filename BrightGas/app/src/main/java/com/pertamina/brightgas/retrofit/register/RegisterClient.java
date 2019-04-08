package com.pertamina.brightgas.retrofit.register;

import android.content.Context;
import android.util.Log;

import com.pertamina.brightgas.AppLocal;
import com.pertamina.brightgas.retrofit.ApiClient;
import com.pertamina.brightgas.retrofit.ApiInterface;
import com.pertamina.brightgas.retrofit.login.LoginRequest;
import com.pertamina.brightgas.retrofit.login.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterClient {

    private static final String TAG = "register_client";

    private RegisterInterface mRegisterInterface;
    private RegisterResponse mRegisterResponse;
    private LoginResponse mLoginResponse;
    private Context mContext;

    public RegisterClient(Context context, RegisterInterface registerInterface) {
        mContext = context;
        mRegisterInterface = registerInterface;
    }

    public void register(final RegisterRequest registerRequest) {
        ApiInterface client = ApiClient.getClient().create(ApiInterface.class);
        Call<RegisterResponse> call = client.register(registerRequest);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                Log.d(TAG, "RETROFIT RESPONSE OK");
                Log.d(TAG, "HTTP RAW: " + response.raw());
                Log.d(TAG, "HTTP CODE: " + response.code());
                Log.d(TAG, "HTTP HEADERS: " + response.headers());
                if (response.isSuccessful()) {
                    RegisterResponse registerResponse = response.body();
                    mRegisterResponse = registerResponse;
                    login(registerRequest.getEmail(), registerRequest.getPassword());
                } else {
                    Log.d(TAG, "HTTP ERROR BODY: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Log.d(TAG, "RETROFIT RESPONSE ERROR");
            }
        });
    }

    private void login(String username, String password) {
        ApiInterface client = ApiClient.getClient().create(ApiInterface.class);
        Call<LoginResponse> call = client.login(new LoginRequest(
                username,
                password
        ));

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                Log.d(TAG, "RETROFIT RESPONSE OK");
                Log.d(TAG, "HTTP RAW: " + response.raw());
                Log.d(TAG, "HTTP CODE: " + response.code());
                Log.d(TAG, "HTTP HEADERS: " + response.headers());

                if (response.isSuccessful()) {
                    LoginResponse registrant = response.body();
                    AppLocal.storeToken(mContext, registrant.getToken());
                    mLoginResponse = registrant;
                    mRegisterInterface.retrofitRegister(mRegisterResponse, mLoginResponse);
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
