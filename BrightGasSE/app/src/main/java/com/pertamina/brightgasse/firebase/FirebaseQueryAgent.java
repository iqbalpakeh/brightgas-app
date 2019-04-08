package com.pertamina.brightgasse.firebase;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.pertamina.brightgasse.BaseActivity;
import com.pertamina.brightgasse.R;
import com.pertamina.brightgasse.firebase.models.Package;
import com.pertamina.brightgasse.model.SimpleOrder;

import java.util.ArrayList;

public class FirebaseQueryAgent extends FirebaseLoader {

    public interface FirebaseQueryAgentLoadable {

        void onAllAgentsReady(DataSnapshot dataSnapshot);

        void onAssignAgentReady();
    }

    private FirebaseQueryAgentLoadable mFirebaseQueryAgentLoadable;

    public FirebaseQueryAgent(Context context, FirebaseQueryAgentLoadable firebaseQueryAgentLoadable) {
        super();
        mFirebaseQueryAgentLoadable = firebaseQueryAgentLoadable;
        mContext = context;
        mDataSnapshotList = new ArrayList<>();
    }

    public void queryAllAgents() {
        mDatabase.child(PATH_ROOT_AGENTS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mFirebaseQueryAgentLoadable.onAllAgentsReady(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled", databaseError.toException());
                        Toast.makeText(mContext, mContext.getString(R.string.str_Query_gagal), Toast.LENGTH_SHORT).show();
                        ((BaseActivity) mContext).showLoading(false);
                    }
                });
    }

    public void assignAgent(final SimpleOrder order, final String selectedAgentId) {

        mDatabase.child(PATH_ROOT_ORDERS).child(order.customerId).child(order.id).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                Log.d(TAG, "doTransaction...");

                Package aPackage = mutableData.getValue(Package.class);

                if (aPackage == null) {
                    return Transaction.success(mutableData);
                }

                Log.d(TAG, "statusId = " + aPackage.statusId);
                Log.d(TAG, "agenId = " + aPackage.agentId);

                aPackage.statusId = Package.STATUS_DIPROSES + "";
                aPackage.agentId = selectedAgentId;

                Log.d(TAG, "statusId = " + aPackage.statusId);
                Log.d(TAG, "agenId = " + aPackage.agentId);

                mutableData.setValue(aPackage);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean result, DataSnapshot dataSnapshot) {
                if (result) {
                    Log.d(TAG, "Transaction completed");
                    Log.d(TAG, "Data: " + dataSnapshot.toString());
                    mFirebaseQueryAgentLoadable.onAssignAgentReady();
                } else {
                    Log.d(TAG, "onCancelled", databaseError.toException());
                    Toast.makeText(mContext, mContext.getString(R.string.str_Query_gagal), Toast.LENGTH_SHORT).show();
                    ((BaseActivity) mContext).showLoading(false);
                }
            }
        });
    }

}
