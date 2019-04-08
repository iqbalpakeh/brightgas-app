package com.pertamina.brightgasse.firebase;

import android.content.Context;

public class FirebaseLogout extends FirebaseLoader {

    private static final String TAG = "firebase_logout";

    public FirebaseLogout(Context context, FirebaseLoadable firebaseLoadable) {
        super();
        mFirebaseLoadable = firebaseLoadable;
        mContext = context;
    }

    public void logout() {
        mAuth.signOut();
        mFirebaseLoadable.setFirebaseData(null);
    }

}
