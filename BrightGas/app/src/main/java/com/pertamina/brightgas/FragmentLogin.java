package com.pertamina.brightgas;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.pertamina.brightgas.firebase.FirebaseLoadable;
import com.pertamina.brightgas.firebase.FirebaseLogin;
import com.pertamina.brightgas.firebase.models.Customer;
import com.pertamina.brightgas.retrofit.login.LoginClient;
import com.pertamina.brightgas.retrofit.login.LoginInterface;
import com.pertamina.brightgas.retrofit.login.LoginRequest;
import com.pertamina.brightgas.retrofit.login.LoginResponse;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

public class FragmentLogin extends Fragment
        implements RequestLoaderInterface, InterfaceOnRequestPermissionsResult, FirebaseLoadable, LoginInterface {

    private static final String TAG = "frag_login";

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) getActivity()).setInterfaceOnRequestPermissionsResult(this);
        requestPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        requestPermission();
    }

    private void requestPermission() {
        if (!((BaseActivity) getActivity()).isPermissionGranted(Manifest.permission.READ_PHONE_STATE)) {
            ((BaseActivity) getActivity()).requestPermissions(new String[]{
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        final EditText username = (EditText) rootView.findViewById(R.id.username);
        final EditText password = (EditText) rootView.findViewById(R.id.password);

        View login = rootView.findViewById(R.id.submit);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (((BaseActivity) getActivity()).isValidEditText(username)) {

                    if (((BaseActivity) getActivity()).isValidEditText(password)) {

                        doLogin(username.getText().toString(), password.getText().toString());

                    } else {

                        ((BaseActivity) getActivity()).showDialog("", "Masukan kata sandi terlebih dahulu", "", "Ok");

                    }

                } else {

                    ((BaseActivity) getActivity()).showDialog("", "Masukan nama pengguna terlebih dahulu", "", "Ok");
                }
            }
        });

        View register = rootView.findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseActivity) getActivity()).changeFragment(new FragmentRegister(), false, true);
            }
        });

        View skip = rootView.findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseActivity) getActivity()).changeActivity(MainActivity.class, true);
            }
        });

        return rootView;
    }

    private void doLogin(String username, String password) {

        ((BaseActivity) getActivity()).showLoading(true, "Login");

        if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.LEGACY) {
            new RequestLoader(this).loadRequest(0, new BasicNameValuePair[]{
                    new BasicNameValuePair("username", username),
                    new BasicNameValuePair("password", password),
                    new BasicNameValuePair("imeii", ((BaseActivity) getActivity()).getImei()),
                    new BasicNameValuePair("gcm_id", ((BaseActivity) getActivity()).getRegistrationId())
            }, "login", "customer", true, true);
        }

        if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.FIREBASE) {
            new FirebaseLogin(getActivity(), this).login(new BasicNameValuePair[]{
                    new BasicNameValuePair("username", username),
                    new BasicNameValuePair("password", password),
                    new BasicNameValuePair("imeii", ((BaseActivity) getActivity()).getImei()),
                    new BasicNameValuePair("gcm_id", ((BaseActivity) getActivity()).getRegistrationId())
            });
        }

        if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.SWAGGER) {
            new LoginClient(getContext(), this).login(new LoginRequest(
                    username,
                    password
            ));
        }

    }

    @Override
    public void setData(int index, String result, View[] impactedView) {
        ((BaseActivity) getActivity()).showLoading(false);
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("status") == 1) {
                User.id = jsonObject.getString("customer_id");
                User.name = jsonObject.getString("name");
                Data.self.insertUser();
                ((BaseActivity) getActivity()).changeActivity(MainActivity.class);
            } else {
                ((BaseActivity) getActivity()).showDialog("Error", jsonObject.getString("message"), "", "Ok");
            }
        } catch (Exception ex) {
            Log.d(TAG, ex.toString());
            ((BaseActivity) getActivity()).showDialog("Error", ex.toString(), "", "Ok");
        }
    }

    @Override
    public void setFirebaseData(DataSnapshot dataSnapshot) {
        ((BaseActivity) getActivity()).showLoading(false);
        Customer customer = dataSnapshot.getValue(Customer.class);
        User.id = customer.uid;
        User.name = customer.name;
        User.picture = customer.pictureUrl;
        Data.self.insertUser();
        ((BaseActivity) getActivity()).changeActivity(MainActivity.class);
    }

    @Override
    public void retrofitLogin(LoginResponse response) {
        ((BaseActivity) getActivity()).showLoading(false);
        User.id = response.getData().getUsername();
        User.name = response.getData().getName();
        User.picture = "no_url"; //todo: request api
        Data.self.insertUser();
        ((BaseActivity) getActivity()).changeActivity(MainActivity.class);
    }
}
