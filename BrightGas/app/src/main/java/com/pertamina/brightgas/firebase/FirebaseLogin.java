package com.pertamina.brightgas.firebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pertamina.brightgas.BaseActivity;
import com.pertamina.brightgas.R;
import com.pertamina.brightgas.firebase.models.Customer;

import org.apache.http.message.BasicNameValuePair;

public class FirebaseLogin extends FirebaseLoader {

    private static final String TAG = "firebase_login";

    private final int POS_EMAIL = 0;
    private final int POS_PASSWORD = 1;

    public FirebaseLogin(Context context, FirebaseLoadable firebaseLoadable) {
        super();
        mFirebaseLoadable = firebaseLoadable;
        mContext = context;
    }

    public void login(final BasicNameValuePair[] inputs) {

        String email = inputs[POS_EMAIL].getValue();
        String password = inputs[POS_PASSWORD].getValue();

        Log.d(TAG, "email = " + email);
        Log.d(TAG, "password = " + password);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((BaseActivity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            queryUserCredential(task.getResult().getUser().getUid());
                        } else {
                            Toast.makeText(mContext, mContext.getString(R.string.str_Login_gagal), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void queryUserCredential(final String uid) {

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Customer customer = dataSnapshot.getValue(Customer.class);
                Log.d(TAG, "user.name = " + customer.name);
                Log.d(TAG, "user.email = " + customer.email);
                Log.d(TAG, "user.phoneNumber = " + customer.phoneNumber);
                Log.d(TAG, "user.gender = " + customer.gender);
                Log.d(TAG, "user.birthdate = " + customer.birthdate);

                mFirebaseLoadable.setFirebaseData(dataSnapshot);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
                ((BaseActivity)mContext).showLoading(false);
            }
        };
        mDatabase.child(PATH_ROOT_USERS).child(uid).addListenerForSingleValueEvent(listener);
    }

}
