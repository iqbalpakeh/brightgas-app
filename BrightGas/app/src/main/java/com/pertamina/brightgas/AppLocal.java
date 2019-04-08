package com.pertamina.brightgas;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

public class AppLocal {

    private static final String APP_CONTEXT = "app_context";

    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String DEF_LATITUDE = "-6.229728";
    private static final String DEF_LONGITUDE = "106.689431";

    private static final String TOKEN = "token";

    public static void storeLocation(Context context, double latitude, double longitude) {
        SharedPreferences appContext = context.getSharedPreferences(APP_CONTEXT, 0);
        SharedPreferences.Editor editor = appContext.edit();
        editor.putString(LATITUDE, String.valueOf(latitude));
        editor.putString(LONGITUDE, String.valueOf(longitude));
        editor.apply();
    }

    public static Location getLocation(Context context) {
        SharedPreferences appContext = context.getSharedPreferences(APP_CONTEXT, 0);
        Location location = new Location("");
        location.setLatitude(Double.parseDouble(appContext.getString(LATITUDE, DEF_LATITUDE)));
        location.setLongitude(Double.parseDouble(appContext.getString(LONGITUDE, DEF_LONGITUDE)));
        return location;
    }

    public static void storeToken(Context context, String token) {
        SharedPreferences appContext = context.getSharedPreferences(APP_CONTEXT, 0);
        SharedPreferences.Editor editor = appContext.edit();
        editor.putString(TOKEN, token);
        editor.apply();
    }

    public static String getToken(Context context) {
        SharedPreferences appContext = context.getSharedPreferences(APP_CONTEXT, 0);
        return "Bearer " + appContext.getString(TOKEN, "");
    }

}
