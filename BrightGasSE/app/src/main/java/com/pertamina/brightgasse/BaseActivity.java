package com.pertamina.brightgasse;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.model.LatLng;

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
    public static final String PREF_NAME = "prefnamecleaningservice";
    public static final String PROPERTY_REG_ID = "PROPERTYREGID";
    public static final int GCM_REGISTER = 4;

    FloatingActionButton fab;
    ProgressDialog progressDialog;
    public int colorOk;
    public int colorNotOk;
    public int colorWhite;
    public int colorAlpha;
    InterfaceFab interfaceFab;
    InterfaceMenu interfaceMenu;
    private InterfaceOnRequestPermissionsResult interfaceOnRequestPermissionsResult;

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setMenuTitle();
        }
    };

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
        Log.d("gugum", permissionKey + " IS NOT GRANTED");
        return false;
    }

    public void requestPermissions(String[] permissionsKey) {
        ActivityCompat.requestPermissions(this, permissionsKey, 0);
    }

    public String getCalculatedPrice(long input) {
        String millionString = "";
        String thousandString = "";
        String unitString = "";

        long million = input / 1000000;
        if (million > 0) {
            millionString = million + "";
        }
        long thousand = (input % 1000000) / 1000;
        if (thousand > 0) {
            if (million > 0 && thousand < 100) {
                thousandString = "0" + thousand;
            } else {
                thousandString = thousand + "";
            }

        } else {
            if (million != 0) {
                thousandString = "000";
            }
        }
        long unit = (input % 1000);
        if (unit > 0) {
            if (thousand > 0 || million > 0) {
                if (unit < 100) {
                    unitString = "0" + unit;
                } else {
                    unitString = unit + "";
                }
            } else {
                unitString = unit + "";
            }
        } else {
            if (thousand > 0 || million > 0) {
                unitString = "000";
            }
        }
        if (millionString.length() > 0) {
            return millionString + "." + thousandString + "." + unitString;
        } else if (thousandString.length() > 0) {
            return thousandString + "." + unitString;
        } else if (unitString.length() > 0) {
            return unitString;
        } else {
            return "0";
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.exaditama.cleaningservice");
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new Data(this);
        colorOk = ContextCompat.getColor(this, R.color.colorAccent);
        colorNotOk = ContextCompat.getColor(this, R.color.colorPrimaryDark);
        colorWhite = ContextCompat.getColor(this, R.color.colorWhite);
        colorAlpha = ContextCompat.getColor(this, R.color.LightOptionBgAlpha);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.exaditama.ekes",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("gugum", "KEY HASH : " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        String regId = null;
        try {
            regId = getRegistrationId();
            Log.d("gugum", "GET REG ID : " + regId);
        } catch (Exception ex) {
            Log.d("gugum", "FAILED GET REG ID : " + ex.toString());
        }

        if (regId == null || regId.isEmpty())
            new GCMTask().executeOnExecutor(new ThreadPoolExecutor(5, 10, 5000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>()), null);
        else
            Log.d("gugum", "REG ID PREF : " + regId + " LENGTH : " + regId.length());
    }

    public void setInterfaceMenu(InterfaceMenu interfaceMenu) {
        this.interfaceMenu = interfaceMenu;
    }

    public void setMenuTitle() {
        interfaceMenu.setMenuTitle();
    }

    public void setInterfaceFab(InterfaceFab interfaceFab) {
        this.interfaceFab = interfaceFab;
    }

    public void setDefaultFabImplementation() {
        interfaceFab.setDefaultFab();
    }

    public void changeActivity(Class input, boolean isFinish) {
        Data.self.close();
        Intent intent = new Intent(this, input);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        if (isFinish)
            finish();
    }

    public void changeActivity(Class input) {
        changeActivity(input, true);
    }

    public void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    public boolean isValidEditText(EditText editText) {
        if (editText.getText().toString().length() > 0) {
            return true;
        }
        return false;
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
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
//            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
//            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

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
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(this);
            }
            progressDialog.setMessage(message);
            progressDialog.setCancelable(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        } else {
            if (progressDialog != null) {
                progressDialog.dismiss();
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

    public void setFabEnabled(boolean val) {
        if (fab != null) {
            if (val) {
                fab.setVisibility(View.VISIBLE);
            } else {
                fab.setVisibility(View.GONE);
            }
        }
    }

    public void setFabIcon(Bitmap imageBitmap) {
        if (imageBitmap == null) {
            Log.d("gugum", "IMAGE BITMAP FAB IS NULL");
        } else {
            Log.d("gugum", "IMAGE BITMAP FAB IS NOT NULL");
        }
        Drawable drawable = new BitmapDrawable(imageBitmap);
        fab.setImageDrawable(drawable);
    }

    public void setFabIcon(int resId) {
        fab.setImageResource(resId);
    }

    public String getPath(final Uri uri) {

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(this, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(this, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{
                            split[1]
                    };

                    return getDataColumn(this, contentUri, selection, selectionArgs);
                }
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(this, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String getFilePath(Uri uri) {
        int currentApiVersion;
        try {
            currentApiVersion = Build.VERSION.SDK_INT;
        } catch (NumberFormatException e) {
            //API 3 will crash if SDK_INT is called
            currentApiVersion = 3;
        }
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {
            String filePath = "";
            String wholeID = DocumentsContract.getDocumentId(uri);

            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];

            String[] column = {MediaStore.Images.Media.DATA};

            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";

            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column, sel, new String[]{id}, null);

            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
            return filePath;
        } else if (currentApiVersion <= Build.VERSION_CODES.HONEYCOMB_MR2 && currentApiVersion >= Build.VERSION_CODES.HONEYCOMB)

        {
            String[] proj = {MediaStore.Images.Media.DATA};
            String result = null;

            CursorLoader cursorLoader = new CursorLoader(
                    this,
                    uri, proj, null, null, null);
            Cursor cursor = cursorLoader.loadInBackground();

            if (cursor != null) {
                int column_index =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                result = cursor.getString(column_index);
            }
            return result;
        } else {

            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
            int column_index
                    = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
    }

    public String getValidString(String val) {
        if (val == null)
            return "";
        else if (val.equals("null"))
            return "";
        else
            return val;
    }

    public boolean isValidString(String val) {
        if (val == null)
            return false;
        else if (val.isEmpty() || val.equals("null")) {
            return false;
        }
        return true;
    }

    public String getRegistrationId() throws Exception {
        final SharedPreferences prefs =
                getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            return "";
        }

        return registrationId;
    }

    String getDeviceInfo() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("os_version", System.getProperty("os.version") + "(" + Build.VERSION.INCREMENTAL + ")");
            jsonObject.put("os_api_level", Build.VERSION.RELEASE + "(" + Build.VERSION.SDK_INT + ")");
            jsonObject.put("device", Build.DEVICE);
            jsonObject.put("model_and_product", Build.MODEL + " (" + Build.PRODUCT + ")");
            jsonObject.put("manufacturer", Build.MANUFACTURER);
            return jsonObject.toString();
        } catch (Exception ex) {
            Log.d("gugum", ex.toString());
        }
        return null;
    }

//    void sendRegistrationId(String id){
//        new RequestLoader(this).loadRequest(GCM_REGISTER, new BasicNameValuePair[]{
//                new BasicNameValuePair("id", id),
//                new BasicNameValuePair("info", getDeviceInfo()),
//        }, "device", "checkdevice", true);
//    }

    private void storeRegistrationId(String regId) throws Exception {
        final SharedPreferences prefs =
                getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.apply();
    }


//    @Override
//    public void setData(int index, String result, View[] impactedViews) {
//
//    }

    class GCMTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String regId = null;
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(BaseActivity.this);
            try {
                regId = gcm.register(Network.GCM_SENDER_ID);
                Log.d("gugum", "REG ID : " + regId);
            } catch (Exception ex) {
                Log.d("gugum", "REGISTER GCM FAILED : " + ex.toString());
            }
            return regId;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null && !s.isEmpty()) {
//                sendRegistrationId(s);
                try {
                    storeRegistrationId(s);
                } catch (Exception ex) {
                    Log.d("gugum", "FAILED STORE REG ID : " + ex.toString());
                }
            }
            super.onPostExecute(s);
        }
    }

    public AnimationSet createAnimationSet(Animation[] listAnimation) {
        return createAnimationSet(listAnimation, false);
    }

    public AnimationSet createAnimationSet(Animation[] listAnimation, Animation.AnimationListener animationListener) {
        return createAnimationSet(listAnimation, false, null, animationListener);
    }

    public AnimationSet createAnimationSet(Animation[] listAnimation, boolean isRepeat) {
        return createAnimationSet(listAnimation, isRepeat, new AccelerateInterpolator());
    }

    public AnimationSet createAnimationSet(Animation[] listAnimation, boolean isRepeat, Interpolator interpolator) {
        return createAnimationSet(listAnimation, isRepeat, interpolator, null);
    }

    public AnimationSet createAnimationSet(Animation[] listAnimation, boolean isRepeat, Interpolator interpolator, Animation.AnimationListener animationListener) {
        AnimationSet animationSet = new AnimationSet(true);
        if (interpolator != null) {
            animationSet.setInterpolator(interpolator);
        }
        animationSet.setFillAfter(true);
        for (Animation anim : listAnimation) {
            animationSet.addAnimation(anim);
        }
        if (isRepeat) {
            animationSet.setRepeatCount(Animation.INFINITE);
        }
        if (animationListener != null) {
            animationSet.setAnimationListener(animationListener);
        }
        return animationSet;
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

    public ScaleAnimation createScaleAnimation(float fromX, float toX, float fromY, float toY, float pivotX, float pivotY, long duration, Animation.AnimationListener listener, int count) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(fromX, toX, fromY, toY, Animation.RELATIVE_TO_SELF, pivotX, Animation.RELATIVE_TO_SELF, pivotY);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setRepeatCount(count);
        if (duration > 0)
            scaleAnimation.setDuration(duration);
        if (listener != null)
            scaleAnimation.setAnimationListener(listener);
        return scaleAnimation;
    }

    public ScaleAnimation createScaleAnimation(float fromX, float toX, float fromY, float toY, float pivotX, float pivotY, long duration, Animation.AnimationListener listener) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(fromX, toX, fromY, toY, Animation.RELATIVE_TO_SELF, pivotX, Animation.RELATIVE_TO_SELF, pivotY);
        scaleAnimation.setFillAfter(true);
        if (duration > 0)
            scaleAnimation.setDuration(duration);
        if (listener != null)
            scaleAnimation.setAnimationListener(listener);
        return scaleAnimation;
    }

    public ScaleAnimation createScaleAnimation(float fromX, float toX, float fromY, float toY, float pivotX, float pivotY, long duration) {
        return createScaleAnimation(fromX, toX, fromY, toY, pivotX, pivotY, duration, null);
    }

    public AlphaAnimation createWaiterAnimetion(long duration, Animation.AnimationListener listener) {
        return createAlphaAnimation(1, 1, duration, listener);
    }

    public AlphaAnimation createAlphaAnimation(float fromAlpha, float toAlpha, long duration, Animation.AnimationListener listener, int count) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(fromAlpha, toAlpha);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setDuration(duration);
        alphaAnimation.setRepeatCount(count);
        if (listener != null)
            alphaAnimation.setAnimationListener(listener);
        return alphaAnimation;
    }

    public AlphaAnimation createAlphaAnimation(float fromAlpha, float toAlpha, long duration, Animation.AnimationListener listener) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(fromAlpha, toAlpha);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setDuration(duration);
        if (listener != null)
            alphaAnimation.setAnimationListener(listener);
        return alphaAnimation;
    }

    public AlphaAnimation createAlphaAnimation(float fromAlpha, float toAlpha, long duration) {
        return createAlphaAnimation(fromAlpha, toAlpha, duration, null);
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

    public TranslateAnimation createTranslateAnimation(float fromX, float toX, float fromY, float toY, long duration, Animation.AnimationListener listener) {
        return createTranslateAnimation(fromX, toX, fromY, toY, duration, null, listener);
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
            Log.d("gugum direction", e.toString());
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

}
