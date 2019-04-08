package com.pertamina.brightgasdriver.firebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.pertamina.brightgasdriver.firebase.model.Package;

public class FirebasePendingOrder extends FirebaseLoader {

    public static final int PENDING_ORDER_SUCCESS = 0;
    public static final int PENDING_ORDER_FAILED = -1;

    public interface FirebasePendingOrderLoadable {
        void pendingOrderReady(int result);
    }

    private FirebasePendingOrderLoadable mFirebasePendingOrderLoadable;

    public FirebasePendingOrder(Context context, FirebasePendingOrderLoadable implementer) {
        super();
        mContext = context;
        mFirebasePendingOrderLoadable = implementer;
    }

    public void pendingOrder(final String customerId, final String orderId, String pendingRemarks) {
        mDatabase.child(PATH_ROOT_ORDERS).child(customerId).child(orderId).child("pendingRemarks").setValue(pendingRemarks)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pendingOrder(customerId, orderId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure", e);
                        mFirebasePendingOrderLoadable.pendingOrderReady(PENDING_ORDER_FAILED);
                    }
                });
    }

    private void pendingOrder(String customerId, String orderId) {
        mDatabase.child(PATH_ROOT_ORDERS).child(customerId).child(orderId).child("statusId").setValue(Package.STATUS_DIPROSES + "")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mFirebasePendingOrderLoadable.pendingOrderReady(PENDING_ORDER_SUCCESS);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure", e);
                        mFirebasePendingOrderLoadable.pendingOrderReady(PENDING_ORDER_FAILED);
                    }
                });
    }

}
