package com.progremastudio.apipilot;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.progremastudio.apipilot.retrofit.ApiClient;
import com.progremastudio.apipilot.retrofit.ApiInterface;
import com.progremastudio.apipilot.retrofit.LoginRequest;
import com.progremastudio.apipilot.retrofit.LoginResponse;
import com.progremastudio.apipilot.retrofit.RegisterRequest;
import com.progremastudio.apipilot.retrofit.RegisterResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityFragment extends Fragment {

    private static String TAG = "frag_main";

    private EditText mUserName;
    private EditText mUserEmail;
    private EditText mPassword;
    private EditText mPasswordConfirmation;
    private EditText mUserGender;
    private EditText mUserDob;
    private EditText mUserAddress;
    private EditText mUserPhone;
    private TextView mHttpResponseViewer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mUserName = (EditText) view.findViewById(R.id.user_name);
        mUserEmail = (EditText) view.findViewById(R.id.user_email);
        mPassword = (EditText) view.findViewById(R.id.user_password);
        mPasswordConfirmation = (EditText) view.findViewById(R.id.user_password_confirmation);
        mUserGender = (EditText) view.findViewById(R.id.user_gender);
        mUserDob = (EditText) view.findViewById(R.id.user_dob);
        mUserAddress = (EditText) view.findViewById(R.id.user_address);
        mUserPhone = (EditText) view.findViewById(R.id.user_phone);
        mHttpResponseViewer = (TextView) view.findViewById(R.id.http_response);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postRegister();
            }
        });

        return view;
    }

    private void postRegister() {

        ((MainActivity) getActivity()).showProgressDialog();
        mHttpResponseViewer.setText("");

        ApiInterface apiPostService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<RegisterResponse> call = apiPostService.postRegister(new RegisterRequest(
                mUserName.getText().toString(),
                mUserEmail.getText().toString(),
                mPassword.getText().toString(),
                mPasswordConfirmation.getText().toString(),
                mUserGender.getText().toString(),
                mUserDob.getText().toString(),
                mUserAddress.getText().toString(),
                mUserPhone.getText().toString()
        ));

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {

                ((MainActivity) getActivity()).hideProgressDialog();

                Log.d(TAG, "RETROFIT RESPONSE OK");
                Log.d(TAG, "HTTP RAW: " + response.raw());
                Log.d(TAG, "HTTP CODE: " + response.code());
                Log.d(TAG, "HTTP HEADERS: " + response.headers());

                mHttpResponseViewer.setText(response.raw().toString());

                if (response.isSuccessful()) {
                    RegisterResponse registrant = response.body();
                    Log.d(TAG, "HTTP BODY: " + response.body());
                    Log.d(TAG, "status:" + registrant.getStatus());
                    Log.d(TAG, "message:" + registrant.getMessage());
                    Log.d(TAG, "username:" + registrant.getData().getUsername());
                    Log.d(TAG, "email:" + registrant.getData().getEmail());
                    Log.d(TAG, "firstname:" + registrant.getData().getFirstname());
                    Log.d(TAG, "lastname:" + registrant.getData().getLastname());
                    Log.d(TAG, "roles:" + registrant.getData().getRoles());

                    postLogin();
                } else {
                    Log.d(TAG, "HTTP ERROR BODY: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                ((MainActivity) getActivity()).hideProgressDialog();
                Log.d(TAG, "RETROFIT RESPONSE ERROR");
            }
        });

    }

    private void postLogin() {

        ((MainActivity) getActivity()).showProgressDialog();

        ApiInterface apiPostService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<LoginResponse> call = apiPostService.postLogin(new LoginRequest(
                mUserName.getText().toString(),
                mPassword.getText().toString()
        ));

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                ((MainActivity) getActivity()).hideProgressDialog();

                Log.d(TAG, "RETROFIT RESPONSE OK");
                Log.d(TAG, "HTTP RAW: " + response.raw());
                Log.d(TAG, "HTTP CODE: " + response.code());
                Log.d(TAG, "HTTP HEADERS: " + response.headers());

                mHttpResponseViewer.setText(response.raw().toString());

                if (response.isSuccessful()) {
                    LoginResponse registrant = response.body();
                    Log.d(TAG, "HTTP BODY: " + response.body());
                    Log.d(TAG, "statusCode:" + registrant.getStatusCode());
                    Log.d(TAG, "token:" + registrant.getToken());
                    Log.d(TAG, "expiration:" + registrant.getExpiration());
                    Log.d(TAG, "username:" + registrant.getData().getUsername());
                    Log.d(TAG, "email:" + registrant.getData().getEmail());
                    Log.d(TAG, "firstname:" + registrant.getData().getFirstname());
                    Log.d(TAG, "lastname:" + registrant.getData().getLastname());
                    Log.d(TAG, "roles:" + registrant.getData().getRoles());
                } else {
                    Log.d(TAG, "HTTP ERROR BODY: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                ((MainActivity) getActivity()).hideProgressDialog();
                Log.d(TAG, "RETROFIT RESPONSE ERROR");
            }
        });

    }
}
