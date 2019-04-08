package com.pertamina.brightgasdriver.firebase;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pertamina.brightgasdriver.BaseActivity;

public class FirebasePinVerification extends FirebaseLoader {

    public static final int PIN_VERIFICATION_SUCCESS = 0;
    public static final int PIN_VERIFICATION_FAILED = -1;

    public interface FirebasePinVerificationLoadable {
        void pinVerificationResult(int result);
    }

    private FirebasePinVerificationLoadable mFirebasePinVerificationLoadable;

    public FirebasePinVerification(Context context, FirebasePinVerificationLoadable implementer) {
        super();
        mContext = context;
        mFirebasePinVerificationLoadable = implementer;
    }

    public void verifyPin(String customerId, String orderId, final String pin) {
        mDatabase.child(PATH_ROOT_ORDERS).child(customerId).child(orderId).child("pinCode")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String pinValue = dataSnapshot.getValue().toString();
                if(pin.equals(pinValue)) {
                    mFirebasePinVerificationLoadable.pinVerificationResult(PIN_VERIFICATION_SUCCESS);
                } else {
                    mFirebasePinVerificationLoadable.pinVerificationResult(PIN_VERIFICATION_FAILED);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled", databaseError.toException());
                ((BaseActivity) mContext).showLoading(false);
            }
        });
    }

}
