package com.pertamina.brightgasagen.firebase;

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
import com.pertamina.brightgasagen.BaseActivity;
import com.pertamina.brightgasagen.R;

import java.util.ArrayList;

public class FirebaseLogin extends FirebaseLoader {

    public static final int OFFSET_CREDENTIAL = 0;
    public static final int OFFSET_ADDRESS = 1;

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
                            queryAgentCredential(task.getResult().getUser().getUid());
                        } else {
                            Toast.makeText(mContext, mContext.getString(R.string.str_Login_gagal), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void queryAgentCredential(final String uid) {
        mDatabase.child(PATH_ROOT_AGENTS).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "agent: " + dataSnapshot.toString());
                mDataSnapshotList.add(OFFSET_CREDENTIAL, dataSnapshot);
                queryAgentAddress(uid);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
                ((BaseActivity) mContext).showLoading(false);
            }
        });
    }

    private void queryAgentAddress(final String uid) {
        mDatabase.child(PATH_ROOT_ADDRESS).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "agent_address:" + dataSnapshot.toString());
                mDataSnapshotList.add(OFFSET_ADDRESS, dataSnapshot);
                mFirebaseLoadable.setFirebaseData(mDataSnapshotList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
                ((BaseActivity) mContext).showLoading(false);
            }
        });


    }

}
