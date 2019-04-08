package com.pertamina.brightgas;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.model.LatLng;
import com.pertamina.brightgas.gcm.MyGcmListenerService;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "base_activity";
    private static final String PREF_NAME = "pref_name_cleaning_service";
    private static final String PROPERTY_REG_ID = "PROPERTY_REG_ID";

    public InterfaceMenu interfaceMenu;

    private ProgressDialog mProgressDialog;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setMenuTitle(intent);
        }
    };

    private InterfaceOnRequestPermissionsResult interfaceOnRequestPermissionsResult;

    public void setInterfaceOnRequestPermissionsResult(InterfaceOnRequestPermissionsResult interfaceOnRequestPermissionsResult) {
        this.interfaceOnRequestPermissionsResult = interfaceOnRequestPermissionsResult;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (interfaceOnRequestPermissionsResult != null)
            interfaceOnRequestPermissionsResult.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public boolean isPermissionGranted(String permissionKey) {
        if (ContextCompat.checkSelfPermission(this, permissionKey) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        Log.d(TAG, permissionKey + " IS NOT GRANTED");
        return false;
    }

    public void requestPermissions(String[] permissionsKey) {
        ActivityCompat.requestPermissions(this, permissionsKey, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.pertamina.brightgas");
        LocalBroadcastManager.getInstance(this).registerReceiver((mBroadcastReceiver), filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Data(this);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.pertamina.brightgas", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d(TAG, "KEY HASH : " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        String regId = null;
        try {
            regId = getRegistrationId();
            Log.d(TAG, "GET REG ID : " + regId);
        } catch (Exception ex) {
            Log.d(TAG, "FAILED GET REG ID : " + ex.toString());
        }

        if (regId == null || regId.isEmpty()) {
            loadRegistrationId();
        } else {
            Log.d(TAG, "REG ID PREF : " + regId + " LENGTH : " + regId.length());
        }
    }

    public void loadRegistrationId() {
        new GCMTask().executeOnExecutor(new ThreadPoolExecutor(5, 10, 5000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>()), null);
    }

    public void setInterfaceMenu(InterfaceMenu interfaceMenu) {
        this.interfaceMenu = interfaceMenu;
    }

    public void setMenuTitle(Intent intent) {
        if (intent != null) {
            Log.d(TAG, "INTENT NOT NULL");
            try {
                int type = Integer.parseInt(intent.getStringExtra("type"));
                Log.d(TAG, "TYPE FROM INTENT : " + type);
                if (type != -1) {
                    String raw = intent.getStringExtra("data");
                    if (type == MyGcmListenerService.RATING) {
                        try {
                            JSONObject jsonObject = new JSONObject(raw);
                            String id = jsonObject.getString("id");
                            FragmentTransaksiSelesai fragment = new FragmentTransaksiSelesai();
                            fragment.setData(id);
                            changeFragment(fragment, true, false);
                        } catch (Exception ex) {
                            Log.d(TAG, ex.toString());
                        }
                    }
                }
            } catch (Exception ex) {
                Log.d(TAG, ex.toString());
            }
        }
        if (interfaceMenu != null)
            interfaceMenu.setMenuTitle();
    }

    public void changeActivity(Class input, boolean isFinish) {
        Data.self.close();
        Intent intent = new Intent(this, input);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        if (isFinish) {
            finish();
        }
    }

    public void changeActivity(Class input) {
        changeActivity(input, true);
    }

    public boolean isValidEditText(EditText editText) {
        if (editText.getText().toString().length() > 0) {
            return true;
        }
        return false;
    }

    public boolean isValidTextView(TextView textView) {
        if (textView.getText() != null && textView.getText().toString() != null && textView.getText().toString().length() > 0 && !textView.getText().toString().equalsIgnoreCase("null")) {
            return true;
        }
        return false;
    }

    public String getImei() {
        TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        return mngr.getDeviceId();
    }

    public String getTextFromEditText(EditText editText) {
        if (editText.getText() == null || editText.getText().toString() == null)
            return "";
        return editText.getText().toString();
    }

    public void changeFragment(Fragment fragment, boolean isReset, boolean isAnimate) {
        hideKeyboard();
        FragmentManager fm = getSupportFragmentManager();
        if (isReset) {
            try {
                fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            } catch (Exception ex) {
                Log.d(TAG, ex.toString());
            }
            if (isAnimate) {
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.fragment, fragment)
                        .commit();
            } else {
                fm.beginTransaction()
                        .replace(R.id.fragment, fragment)
                        .commit();
            }
        } else {
            if (isAnimate) {
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.fragment, fragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                fm.beginTransaction()
                        .replace(R.id.fragment, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    public void changeFragment(Fragment fragment, boolean isReset) {
        changeFragment(fragment, isReset, true);
    }

    public void changeFragment(Fragment fragment) {
        changeFragment(fragment, true);
    }

    public void changeFragment(int fragmentId, Fragment fragment, boolean isReset, boolean isAnimate) {
        hideKeyboard();
        FragmentManager fm = getSupportFragmentManager();
        if (isReset) {
            if (isAnimate) {
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(fragmentId, fragment)
                        .commit();
            } else {
                fm.beginTransaction()
                        .replace(fragmentId, fragment)
                        .commit();
            }
        } else {
            if (isAnimate) {
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(fragmentId, fragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                fm.beginTransaction()
                        .replace(fragmentId, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    public void changeFragment(int fragmentId, Fragment fragment, boolean isReset, int animIn, int animOut) {
        hideKeyboard();
        FragmentManager fm = getSupportFragmentManager();
        if (isReset) {
            fm.beginTransaction()
                    .setCustomAnimations(animIn, animOut)
                    .replace(fragmentId, fragment)
                    .commit();
        } else {
            fm.beginTransaction()
                    .setCustomAnimations(animIn, animOut)
                    .replace(fragmentId, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    public void hideKeyboard() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    public void showLoading(boolean value) {
        showLoading(value, "Loading...");
    }

    public void showLoading(boolean value, String message) {
        if (value) {
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(this);
            }
            mProgressDialog.setMessage(message);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
        } else {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
        }
    }

    public void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    public AlertDialog showDialog(String title, String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        return showDialog(title, message, "Ok", "Cancel", okListener, cancelListener);
    }

    public AlertDialog showDialog(String title, String message, String okTitle, String cancelTitle, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(okTitle, okListener);
        builder.setNegativeButton(cancelTitle, cancelListener);
        return builder.show();
    }

    public AlertDialog showDialog(String title, String message, String okTitle, String cancelTitle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(okTitle, null);
        builder.setNegativeButton(cancelTitle, null);
        return builder.show();
    }

    public AlertDialog showDialog(String title, String message, String okTitle, String cancelTitle, String neutralTitle, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener, DialogInterface.OnClickListener neutralListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(okTitle, okListener);
        builder.setNegativeButton(cancelTitle, cancelListener);
        builder.setNeutralButton(neutralTitle, neutralListener);
        return builder.show();
    }

    public AlertDialog showDialog(String title, View customView, String okTitle, String cancelTitle, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(title);
        builder.setView(customView);
        builder.setPositiveButton(okTitle, null);
        builder.setNegativeButton(cancelTitle, cancelListener);
        return builder.show();
    }

    public boolean isValidString(String val) {
        if (val == null)
            return false;
        else if (val.isEmpty() || val.equals("null")) {
            return false;
        }
        return true;
    }

    public String getRegistrationId() {
        String registrationId = null;
        try {
            final SharedPreferences prefs =
                    getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            registrationId = prefs.getString(PROPERTY_REG_ID, "");
            if (registrationId.isEmpty()) {
                return "";
            }
        } catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }

        return registrationId;
    }

    private void storeRegistrationId(String regId) throws Exception {
        final SharedPreferences prefs =
                getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.apply();
    }

    public RotateAnimation createRotateAnimation(float fromDegree, float toDegree, float pivotX, float pivotY, long duration, int repeatCount, Interpolator interpolator) {
        RotateAnimation rotateAnimation = new RotateAnimation(fromDegree, toDegree, Animation.RELATIVE_TO_SELF, pivotX, Animation.RELATIVE_TO_SELF, pivotY);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setDuration(duration);
        if (repeatCount != 0) {
            rotateAnimation.setRepeatCount(Animation.INFINITE);
        }
        if (interpolator != null) {
            rotateAnimation.setInterpolator(interpolator);
        }
        return rotateAnimation;
    }

    public TranslateAnimation createTranslateAnimation(float fromX, float toX, float fromY, float toY, long duration, Interpolator interpolator, Animation.AnimationListener listener) {
        TranslateAnimation translateAnimation = new TranslateAnimation(fromX, toX, fromY, toY);
        translateAnimation.setFillAfter(true);
        translateAnimation.setDuration(duration);
        if (interpolator != null)
            translateAnimation.setInterpolator(interpolator);
        if (listener != null)
            translateAnimation.setAnimationListener(listener);
        return translateAnimation;
    }

    public TranslateAnimation createTranslateAnimation(float fromX, float toX, float fromY, float toY, long duration) {
        return createTranslateAnimation(fromX, toX, fromY, toY, duration, null, null);
    }

    public String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d(TAG, e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    class GCMTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String regId = null;
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(BaseActivity.this);
            try {
                regId = gcm.register(Network.GCM_SENDER_ID);
                Log.d(TAG, "REG ID : " + regId);
            } catch (Exception ex) {
                Log.d(TAG, "REGISTER GCM FAILED : " + ex.toString());
            }
            return regId;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null && !s.isEmpty()) {
                try {
                    storeRegistrationId(s);
                } catch (Exception ex) {
                    Log.d(TAG, "FAILED STORE REG ID : " + ex.toString());
                }
            }
            super.onPostExecute(s);
        }
    }
}
