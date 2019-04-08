package com.pertamina.brightgasdriver.firebase;

import android.content.Context;

import java.util.ArrayList;

public class FirebaseLogout extends FirebaseLoader {

    public FirebaseLogout(Context context, FirebaseLoadable firebaseLoadable) {
        super();
        mFirebaseLoadable = firebaseLoadable;
        mContext = context;
        mDataSnapshotList = new ArrayList<>();
    }

    public void logout() {
        mAuth.signOut();
        mFirebaseLoadable.setFirebaseData(null);
    }
}
