package com.pertamina.brightgasdriver;

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
import com.pertamina.brightgasdriver.firebase.FirebaseLoadable;
import com.pertamina.brightgasdriver.firebase.FirebaseLogin;
import com.pertamina.brightgasdriver.firebase.model.Agent;
import com.pertamina.brightgasdriver.firebase.model.Driver;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentLogin extends Fragment
        implements RequestLoaderInterface, InterfaceOnRequestPermissionsResult, FirebaseLoadable {

    private static final String TAG = "frag_login";

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) getActivity()).setInterfaceOnRequestPermissionsResult(this);
        init();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        init();
    }

    private void init() {

        if (!((BaseActivity) getActivity()).isPermissionGranted(android.Manifest.permission.READ_PHONE_STATE)
                || !((BaseActivity) getActivity()).isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)
                || !((BaseActivity) getActivity()).isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {

            ((BaseActivity) getActivity()).requestPermissions(new String[]{
                    android.Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        View login = rootView.findViewById(R.id.submit);

        final EditText username = (EditText) rootView.findViewById(R.id.username);
        final EditText password = (EditText) rootView.findViewById(R.id.password);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((BaseActivity) getActivity()).isValidEditText(username)) {
                    if (((BaseActivity) getActivity()).isValidEditText(password)) {
                        doLogin(username.getText().toString(), password.getText().toString());
                    } else {
                        ((BaseActivity) getActivity()).showDialog("Pesan", "Masukan kata sandi terlebih dahulu");
                    }
                } else {
                    ((BaseActivity) getActivity()).showDialog("Pesan", "Masukan nama pengguna terlebih dahulu");
                }
            }
        });
        return rootView;
    }

    private void doLogin(String username, String password) {

        Log.d(TAG, "NETWORK URL : " + Network.baseUrl);
        ((BaseActivity) getActivity()).showLoading(true, "Login");

        new RequestLoader(this).loadRequest(0, new BasicNameValuePair[]{
                new BasicNameValuePair("username", username),
                new BasicNameValuePair("password", password),
                new BasicNameValuePair("imeii", ((BaseActivity) getActivity()).getImei()),
                new BasicNameValuePair("gcm_id", ((BaseActivity) getActivity()).getRegistrationId())
        }, "login", "driver", true);

        new FirebaseLogin(getContext(), this).login(username, password);

    }

    @Override
    public void setFirebaseData(ArrayList<DataSnapshot> dataSnapshots) {

        ((BaseActivity) getActivity()).showLoading(false);

        Log.d(TAG, dataSnapshots.get(FirebaseLogin.OFFSET_DRIVER).toString());
        Log.d(TAG, dataSnapshots.get(FirebaseLogin.OFFSET_AGENT).toString());

        Driver driver = dataSnapshots.get(FirebaseLogin.OFFSET_DRIVER).getValue(Driver.class);
        Agent agent = dataSnapshots.get(FirebaseLogin.OFFSET_AGENT).getValue(Agent.class);

        User.id = dataSnapshots.get(FirebaseLogin.OFFSET_DRIVER).getKey();
        User.name = driver.getName();
        User.agenName = agent.getName();
        User.agenId = driver.getAgenId();
        Data.self.insertUser();
        ((BaseActivity) getActivity()).changeActivity(MainActivity.class);

    }

    @Override
    public void setData(int index, String result, View[] impactedView) {
        ((BaseActivity) getActivity()).showLoading(false);
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("status") == 1) {
                User.id = jsonObject.getString("driver_id");
                User.name = jsonObject.getString("name");
                User.agenName = jsonObject.getString("agen");
                User.agenId = jsonObject.getString("agen_id");
                Data.self.insertUser();
                ((BaseActivity) getActivity()).changeActivity(MainActivity.class);
            } else {
                ((BaseActivity) getActivity()).showDialog("Error", jsonObject.getString("message"));
            }
        } catch (Exception ex) {
            Log.d(TAG, ex.toString());
            ((BaseActivity) getActivity()).showDialog("Error", ex.toString());
        }
    }

}
