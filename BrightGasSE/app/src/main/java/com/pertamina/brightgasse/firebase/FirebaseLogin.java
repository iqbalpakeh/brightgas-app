package com.pertamina.brightgasse.firebase;

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
import com.pertamina.brightgasse.BaseActivity;
import com.pertamina.brightgasse.R;
import com.pertamina.brightgasse.firebase.models.Sales;

import java.util.ArrayList;

public class FirebaseLogin extends FirebaseLoader {

    public FirebaseLogin(Context context, FirebaseLoadable firebaseLoadable) {
        super();
        mFirebaseLoadable = firebaseLoadable;
        mContext = context;
        mDataSnapshotList = new ArrayList<>();
    }

    public void login(String email, String password) {

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
        mDatabase.child(PATH_ROOT_SALES).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Sales sales = dataSnapshot.getValue(Sales.class);
                Log.d(TAG, "sales.name: " + sales.name);
                Log.d(TAG, "sales.token: " + sales.token);
                Log.d(TAG, "sales.image: " + sales.image);
                Log.d(TAG, "sales.email: " + sales.email);

                mDataSnapshotList.add(dataSnapshot);
                mFirebaseLoadable.setFirebaseData(mDataSnapshotList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
                ((BaseActivity)mContext).showLoading(false);
            }
        });
    }

}
