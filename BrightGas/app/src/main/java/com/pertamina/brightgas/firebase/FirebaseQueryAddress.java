package com.pertamina.brightgas.firebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pertamina.brightgas.BaseActivity;

public class FirebaseQueryAddress extends FirebaseLoader {

    private static final String TAG = "query_address";

    public interface FirebaseAddressLoadable {
        void setProfilData(DataSnapshot profilSnapshot, DataSnapshot addressSnapshot);
    }

    public interface FirebaseAddressRemovable {
        void removeAddress(int position);
    }

    private FirebaseAddressLoadable mFirebaseAddressLoadable;
    private FirebaseAddressRemovable mFirebaseAddressRemovable;

    private DataSnapshot mProfilSnapshot;
    private DataSnapshot mAddressSnapshot;

    public FirebaseQueryAddress(Context context, FirebaseLoadable firebaseLoadable) {
        super();
        mFirebaseLoadable = firebaseLoadable;
        mContext = context;
    }

    public FirebaseQueryAddress(Context context, FirebaseAddressLoadable firebaseAddressLoadable) {
        super();
        mFirebaseAddressLoadable = firebaseAddressLoadable;
        mContext = context;
    }

    public FirebaseQueryAddress(Context context, FirebaseAddressRemovable firebaseAddressRemovable) {
        super();
        mFirebaseAddressRemovable = firebaseAddressRemovable;
        mContext = context;
    }

    public void query() {
        String uid = mAuth.getCurrentUser().getUid();
        mDatabase.child(PATH_ROOT_ADDRESS).child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, dataSnapshot.toString());
                        mFirebaseLoadable.setFirebaseData(dataSnapshot);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "onCancelled", databaseError.toException());
                    }
                });
    }

    public void queryProfilData() {
        String uid = mAuth.getCurrentUser().getUid();
        mDatabase.child(PATH_ROOT_USERS).child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, dataSnapshot.toString());
                        mProfilSnapshot = dataSnapshot;
                        queryAddress();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "onCancelled", databaseError.toException());
                    }
                });
    }

    private void queryAddress() {
        String uid = mAuth.getCurrentUser().getUid();
        mDatabase.child(PATH_ROOT_ADDRESS).child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, dataSnapshot.toString());
                        mAddressSnapshot = dataSnapshot;
                        mFirebaseAddressLoadable.setProfilData(mProfilSnapshot, mAddressSnapshot);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "onCancelled", databaseError.toException());
                    }
                });
    }

    public void deleteAddress(String addressId, final int position) {
        String uid = mAuth.getCurrentUser().getUid();
        mDatabase.child(PATH_ROOT_ADDRESS).child(uid).child(addressId).removeValue()
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mFirebaseAddressRemovable.removeAddress(position);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onCancelled", e.getCause());
                ((BaseActivity) mContext).showLoading(false);
            }
        });
    }


}
