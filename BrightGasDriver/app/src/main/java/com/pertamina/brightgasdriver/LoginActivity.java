package com.pertamina.brightgasdriver;

import android.os.Bundle;
import android.util.Log;

public class LoginActivity extends BaseActivity {

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
            changeActivity(MainActivity.class, true);
        } else {
            setContentView(R.layout.activity_login);
            changeFragment(new FragmentLogin());
        }
    }
}
