package com.pertamina.brightgas.retrofit.logout;

import android.content.Context;

import com.pertamina.brightgas.AppLocal;

public class LogoutClient {

    // No Swagger API for log out. It just delete local user data and back to login page

    private static final String TAG = "login_client";

    private LogoutInterface mInterface;
    private Context mContext;

    public LogoutClient(Context context, LogoutInterface anInterface) {
        this.mContext = context;
        this.mInterface = anInterface;
    }

    public void logout() {
        AppLocal.storeToken(mContext, "");
        mInterface.retrofitLogOut(true);
    }

}
