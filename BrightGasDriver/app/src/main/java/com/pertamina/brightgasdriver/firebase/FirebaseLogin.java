package com.pertamina.brightgasdriver.firebase;

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
import com.pertamina.brightgasdriver.BaseActivity;
import com.pertamina.brightgasdriver.R;
import com.pertamina.brightgasdriver.firebase.model.Agent;
import com.pertamina.brightgasdriver.firebase.model.Driver;

import java.util.ArrayList;

public class FirebaseLogin extends FirebaseLoader {

    public static final int OFFSET_DRIVER = 0;
    public static final int OFFSET_AGENT = 1;

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
                            queryDriver(task.getResult().getUser().getUid());
                        } else {
                            Toast.makeText(mContext, mContext.getString(R.string.str_Login_gagal), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void queryDriver(final String uid) {
        mDatabase.child(PATH_ROOT_DRIVERS).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d(TAG, "datasnapshot: " + dataSnapshot.toString());

                Driver driver = dataSnapshot.getValue(Driver.class);

                mDataSnapshotList.add(OFFSET_DRIVER, dataSnapshot);
                queryAssociatedAgent(driver.getAgenId());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
                ((BaseActivity) mContext).showLoading(false);
            }
        });
    }

    private void queryAssociatedAgent(String agenId) {
        mDatabase.child(PATH_ROOT_AGENTS).child(agenId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "datasnapshot: " + dataSnapshot.toString());
                Agent agent = dataSnapshot.getValue(Agent.class);

                mDataSnapshotList.add(OFFSET_AGENT, dataSnapshot);
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
