package com.pertamina.brightgas;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.pertamina.brightgas.retrofit.customerprofile.CustomerProfileClient;
import com.pertamina.brightgas.retrofit.customerprofile.CustomerProfileInterface;
import com.pertamina.brightgas.retrofit.customerprofile.CustomerProfileResponse;

public class LoginActivity extends BaseActivity implements CustomerProfileInterface {

    private static final String TAG = "login_activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Data.self.getUser();
        } catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }

        if (User.id != null) {
            if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.SWAGGER) {
                checkTokenExpirationDate();
            } else {
                changeActivity(MainActivity.class, true);
            }
        } else {
            setContentView(R.layout.activity_login);
            changeFragment(new FragmentLogin());
        }
    }

    private void checkTokenExpirationDate() {
        this.showLoading(true, "Loading");
        new CustomerProfileClient(getApplicationContext(), this).showProfile();
    }

    @Override
    public void retrofitShowProfile(CustomerProfileResponse response) {
        this.showLoading(false);
        if (response != null) {
            // successful login
            changeActivity(MainActivity.class, true);
        } else {
            // token is expired
            Toast.makeText(this, "Token is expired", Toast.LENGTH_SHORT).show();
            AppLocal.storeToken(getApplicationContext(), "");
            try {
                Data.self.clearTable(Data.TABLE_USER);
                User.clear();
                changeActivity(LoginActivity.class, true);
            } catch (Exception ex) {
                this.showDialog("Failed Logout", ex.toString());
            }
        }
    }
}
