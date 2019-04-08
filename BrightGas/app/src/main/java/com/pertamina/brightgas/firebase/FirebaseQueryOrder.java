package com.pertamina.brightgas.firebase;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pertamina.brightgas.firebase.models.Package;

public class FirebaseQueryOrder extends FirebaseLoader {

    private static final String TAG = "query_order";

    public interface FirebaseOrderLoadable {
        void setTransactionData(DataSnapshot packageSnapshot,
                                DataSnapshot addressSnapshot,
                                DataSnapshot agentSnapshot,
                                DataSnapshot driverSnapshot);
    }

    private FirebaseOrderLoadable mFirebaseOrderLoadable;

    private DataSnapshot mPackageSnapshot;
    private DataSnapshot mAddressSnapshot;
    private DataSnapshot mAgentSnapshot;
    private DataSnapshot mDriverSnapshot;


    public FirebaseQueryOrder(Context context, FirebaseLoadable firebaseLoadable) {
        super();
        mFirebaseLoadable = firebaseLoadable;
        mContext = context;
    }

    public FirebaseQueryOrder(Context context, FirebaseOrderLoadable firebaseOrderLoadable) {
        super();
        mFirebaseOrderLoadable = firebaseOrderLoadable;
        mContext = context;
    }

    public void query() {

        String uid = mAuth.getCurrentUser().getUid();

        mDatabase.child(PATH_ROOT_ORDERS).child(uid)
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

    public void queryTransaction(String orderId, final String agenId, final String driverId) {

        String uid = mAuth.getCurrentUser().getUid();

        mDatabase.child(PATH_ROOT_ORDERS).child(uid).child(orderId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mPackageSnapshot = dataSnapshot;
                        queryTransaction(agenId, driverId);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "onCancelled", databaseError.toException());
                    }
                });
    }

    private void queryTransaction(String agenId, final String driverId) {
        mDatabase.child(PATH_ROOT_AGENTS).child(agenId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mAgentSnapshot = dataSnapshot;
                        queryTransaction(driverId);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "onCancelled", databaseError.toException());
                    }
                });
    }

    private void queryTransaction(String driverId) {

        String uid = mAuth.getCurrentUser().getUid();

        mDatabase.child(PATH_ROOT_DRIVERS).child(uid).child(driverId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mDriverSnapshot = dataSnapshot;
                        queryAddress(mPackageSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "onCancelled", databaseError.toException());
                    }
                });
    }

    private void queryAddress(DataSnapshot packageSnapshot) {

        String uid = mAuth.getCurrentUser().getUid();
        Package aPackage = packageSnapshot.getValue(Package.class);

        mDatabase.child(PATH_ROOT_ADDRESS).child(uid).child(aPackage.addressId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mAddressSnapshot = dataSnapshot;
                        mFirebaseOrderLoadable.setTransactionData(
                                mPackageSnapshot,
                                mAddressSnapshot,
                                mAgentSnapshot,
                                mDriverSnapshot
                        );
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "onCancelled", databaseError.toException());
                    }
                });
    }


}
