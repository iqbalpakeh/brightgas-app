package com.pertamina.brightgasagen;

import android.app.Application;

//@ReportsCrashes(
//        mailTo = "kordon.kircon@gmail.com",
//        mode = ReportingInteractionMode.DIALOG,
//        resDialogText = R.string.crash_dialog_text,
//        resDialogIcon = android.R.drawable.ic_dialog_info, //optional. default is a warning sign
//        resDialogTitle = R.string.crash_dialog_title // optional. default is your application name
//)

public class MyApplication extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        //ACRA.init(this);
    }
}
