package com.pertamina.brightgasse;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.pertamina.brightgasse.firebase.FirebaseLoadable;
import com.pertamina.brightgasse.firebase.FirebaseLogin;
import com.pertamina.brightgasse.firebase.models.Sales;

import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentLogin extends Fragment implements RequestLoaderInterface, FirebaseLoadable {

    public static String TAG = "frag_login";

    private EditText mUsername;
    private EditText mPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        View login = rootView.findViewById(R.id.submit);

        mUsername = (EditText) rootView.findViewById(R.id.username);
        mPassword = (EditText) rootView.findViewById(R.id.password);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (((BaseActivity) getActivity()).isValidEditText(mUsername)) {

                    if (((BaseActivity) getActivity()).isValidEditText(mPassword)) {

                        doLogin(mUsername.getText().toString(), mPassword.getText().toString());

                    } else {
                        ((BaseActivity) getActivity()).showDialog("", "Masukan kata sandi terlebih dahulu", "", "Ok");
                    }

                } else {
                    ((BaseActivity) getActivity()).showDialog("", "Masukan nama pengguna terlebih dahulu", "", "Ok");
                }
            }
        });

        return rootView;
    }

    private void doLogin(String username, String password) {

        ((BaseActivity) getActivity()).showLoading(true, "Login");

        new FirebaseLogin(getActivity(), this).login(username, password);

    }

    @Override
    public void setData(int index, String result, View[] impactedView) {

        ((BaseActivity) getActivity()).showLoading(false);

        try {

            JSONObject jsonObject = new JSONObject(result);

            if (jsonObject.getBoolean("status")) {
                User.role_id = jsonObject.getInt("roleid");
                User.role_name = jsonObject.getString("rolename");
                User.token = jsonObject.getString("token");
                User.image_url = jsonObject.getString("image");
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

    @Override
    public void setFirebaseData(ArrayList<DataSnapshot> dataSnapshots) {

        ((BaseActivity) getActivity()).showLoading(false);

        Sales sales = dataSnapshots.get(0).getValue(Sales.class);
        Log.d(TAG, "sales.name: " + sales.name);
        Log.d(TAG, "sales.token: " + sales.token);
        Log.d(TAG, "sales.image: " + sales.image);
        Log.d(TAG, "sales.email: " + sales.email);

        User.role_id = 0; // todo: is this variable can be removed?
        User.role_name = sales.name;
        User.token = sales.token;
        User.image_url = sales.image;
        Data.self.insertUser();

        ((BaseActivity) getActivity()).changeActivity(MainActivity.class);

    }
}
