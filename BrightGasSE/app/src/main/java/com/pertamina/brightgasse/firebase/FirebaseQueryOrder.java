package com.pertamina.brightgasse.firebase;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pertamina.brightgasse.BaseActivity;
import com.pertamina.brightgasse.R;
import com.pertamina.brightgasse.firebase.models.Package;

import java.util.ArrayList;

public class FirebaseQueryOrder extends FirebaseLoader {

    public static final int ORDER_OFFSET = 0;
    public static final int CUSTOMER_OFFSET = 1;
    public static final int CUSTOMER_ADDRESS_OFFSET = 2;
    public static final int AGENT_OFFSET = 3;
    public static final int AGENT_ADDRESS_OFFSET = 4;

    private Package mPackage;

    public FirebaseQueryOrder(Context context, FirebaseLoadable firebaseLoadable) {
        super();
        mFirebaseLoadable = firebaseLoadable;
        mContext = context;
        mDataSnapshotList = new ArrayList<>();
    }

    public void queryOrders() {
        mDatabase.child(PATH_ROOT_ORDERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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

    public void queryOrderDetail(String customerId, String orderId) {
        mDatabase.child(PATH_ROOT_ORDERS).child(customerId)
                .child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDataSnapshotList.add(ORDER_OFFSET, dataSnapshot);
                mPackage = dataSnapshot.getValue(Package.class);
                queryCustomer();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
                Toast.makeText(mContext, mContext.getString(R.string.str_Query_gagal), Toast.LENGTH_SHORT).show();
                ((BaseActivity) mContext).showLoading(false);
            }
        });

    }

    private void queryCustomer() {
        mDatabase.child(PATH_ROOT_USERS).child(mPackage.uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mDataSnapshotList.add(CUSTOMER_OFFSET, dataSnapshot);
                        queryCustomerAddress();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "onCancelled", databaseError.toException());
                        Toast.makeText(mContext, mContext.getString(R.string.str_Query_gagal), Toast.LENGTH_SHORT).show();
                        ((BaseActivity) mContext).showLoading(false);
                    }
                });
    }

    private void queryCustomerAddress() {
        mDatabase.child(PATH_ROOT_ADDRESS).child(mPackage.uid)
                .child(mPackage.addressId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDataSnapshotList.add(CUSTOMER_ADDRESS_OFFSET, dataSnapshot);
                queryAgent();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
                Toast.makeText(mContext, mContext.getString(R.string.str_Query_gagal), Toast.LENGTH_SHORT).show();
                ((BaseActivity) mContext).showLoading(false);
            }
        });
    }

    private void queryAgent() {
        mDatabase.child(PATH_ROOT_AGENTS).child(mPackage.agentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mDataSnapshotList.add(AGENT_OFFSET, dataSnapshot);
                        queryAgentAddress();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "onCancelled", databaseError.toException());
                        Toast.makeText(mContext, mContext.getString(R.string.str_Query_gagal), Toast.LENGTH_SHORT).show();
                        ((BaseActivity) mContext).showLoading(false);
                    }
                });
    }

    private void queryAgentAddress() {
        mDatabase.child(PATH_ROOT_ADDRESS).child(mPackage.agentId)
                .child("address_0").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDataSnapshotList.add(AGENT_ADDRESS_OFFSET, dataSnapshot);
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
