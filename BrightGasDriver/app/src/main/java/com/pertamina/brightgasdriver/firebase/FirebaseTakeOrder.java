package com.pertamina.brightgasdriver.firebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.pertamina.brightgasdriver.firebase.model.Package;

public class FirebaseTakeOrder extends FirebaseLoader {

    public static final int TAKE_ORDER_SUCCESS = 0;
    public static final int TAKE_ORDER_FAILED = -1;

    public interface FirebaseTakeOrderLoadable {
        void takeOrderReady(int result);
    }

    private FirebaseTakeOrderLoadable mFirebaseTakeOrderLoadable;

    public FirebaseTakeOrder(Context context, FirebaseTakeOrderLoadable implementer) {
        super();
        mContext = context;
        mFirebaseTakeOrderLoadable = implementer;
    }

    public void takeOrder(String customerId, String orderId) {
        mDatabase.child(PATH_ROOT_ORDERS).child(customerId).child(orderId).child("statusId").setValue(Package.STATUS_DIKIRIM + "")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mFirebaseTakeOrderLoadable.takeOrderReady(TAKE_ORDER_SUCCESS);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure", e);
                        mFirebaseTakeOrderLoadable.takeOrderReady(TAKE_ORDER_FAILED);
                    }
                });
    }

}
