package com.pertamina.brightgas.firebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pertamina.brightgas.R;
import com.pertamina.brightgas.firebase.models.Package;

public class FirebaseStoreOrder extends FirebaseLoader {

    private String mUid;
    private String mKey;

    public FirebaseStoreOrder(Context context, FirebaseLoadable firebaseLoadable) {
        super();
        mFirebaseLoadable = firebaseLoadable;
        mContext = context;
    }

    public void storeOrder(Package aPackage) {

        mUid = mAuth.getCurrentUser().getUid();
        mKey = mDatabase.child(PATH_ROOT_ORDERS).child(mUid).push().getKey();

        mDatabase.child(PATH_ROOT_ORDERS).child(mUid).child(mKey).setValue(aPackage.toMap())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "add new address complete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            queryNewlyCreatedOrder();
                        } else {
                            Toast.makeText(mContext, mContext.getString(R.string.str_Pemesanan_gagal), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void queryNewlyCreatedOrder() {
        mDatabase.child(PATH_ROOT_ORDERS).child(mUid).child(mKey)
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
}
