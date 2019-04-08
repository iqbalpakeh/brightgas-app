package com.pertamina.brightgasse.firebase;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pertamina.brightgasse.BaseActivity;
import com.pertamina.brightgasse.R;

import java.util.ArrayList;

public class FirebaseQueryAddress extends FirebaseLoader {

    public FirebaseQueryAddress(Context context, FirebaseLoadable firebaseLoadable) {
        super();
        mFirebaseLoadable = firebaseLoadable;
        mContext = context;
        mDataSnapshotList = new ArrayList<>();
    }

    public void queryAddress(String uid) {
        mDatabase.child(PATH_ROOT_ADDRESS).child(uid).child("address_0")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot.toString());
                mDataSnapshotList.add(dataSnapshot);
                mFirebaseLoadable.setFirebaseData(mDataSnapshotList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
                Toast.makeText(mContext, mContext.getString(R.string.str_Query_gagal), Toast.LENGTH_SHORT).show();
                ((BaseActivity) mContext).showLoading(false);
            }
        });
    }

}
