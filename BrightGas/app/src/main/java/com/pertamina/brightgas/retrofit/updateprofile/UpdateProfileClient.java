package com.pertamina.brightgas.retrofit.updateprofile;

import android.content.Context;
import android.util.Log;

import com.pertamina.brightgas.AppLocal;
import com.pertamina.brightgas.retrofit.ApiClient;
import com.pertamina.brightgas.retrofit.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileClient {

    private static final String TAG = "update_profile";

    private UpdateProfileInterface mUpdateProfileInterface;
    private UpdateProfileResponse mUpdateProfileResponse;
    private Context mContext;

    public UpdateProfileClient(Context context, UpdateProfileInterface updateProfileInterface) {
        mContext = context;
        mUpdateProfileInterface = updateProfileInterface;
    }

    public void updateProfile(UpdateProfileRequest updateProfileRequest) {
        ApiInterface client = ApiClient.getClient().create(ApiInterface.class);
        Call<UpdateProfileResponse> call = client.updateProfile(
                AppLocal.getToken(mContext),
                updateProfileRequest
        );
        call.enqueue(new Callback<UpdateProfileResponse>() {
            @Override
            public void onResponse(Call<UpdateProfileResponse> call, Response<UpdateProfileResponse> response) {
                Log.d(TAG, "RETROFIT RESPONSE OK");
                Log.d(TAG, "HTTP RAW: " + response.raw());
                Log.d(TAG, "HTTP CODE: " + response.code());
                Log.d(TAG, "HTTP HEADERS: " + response.headers());
                if (response.isSuccessful()) {
                    UpdateProfileResponse updateProfileResponse = response.body();
                    mUpdateProfileInterface.retrofitUpdateProfile(true);
                } else {
                    Log.d(TAG, "HTTP ERROR BODY: " + response.errorBody());
                    mUpdateProfileInterface.retrofitUpdateProfile(false);
                }
            }

            @Override
            public void onFailure(Call<UpdateProfileResponse> call, Throwable t) {
                mUpdateProfileInterface.retrofitUpdateProfile(false);
            }
        });
    }

}
