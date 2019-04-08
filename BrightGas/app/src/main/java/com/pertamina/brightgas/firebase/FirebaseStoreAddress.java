package com.pertamina.brightgas.firebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.pertamina.brightgas.R;
import com.pertamina.brightgas.firebase.models.Address;

import org.apache.http.message.BasicNameValuePair;

public class FirebaseStoreAddress extends FirebaseLoader {

    private static final String TAG = "store_address";

    private final int POS_ADDRESS = 1;
    private final int POS_LATITUDE = 2;
    private final int POS_LONGITUDE = 3;

    public FirebaseStoreAddress(Context context, FirebaseLoadable firebaseLoadable) {
        super();
        mFirebaseLoadable = firebaseLoadable;
        mContext = context;
    }

    public void storeAddress(BasicNameValuePair[] inputs) {

        String uid = mAuth.getCurrentUser().getUid();
        String key = mDatabase.child(PATH_ROOT_ADDRESS).child(uid).push().getKey();

        Address address = new Address(
                inputs[POS_ADDRESS].getValue(),
                inputs[POS_LATITUDE].getValue(),
                inputs[POS_LONGITUDE].getValue()
        );
        mDatabase.child(PATH_ROOT_ADDRESS).child(uid).child(key).setValue(address.toMap())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "add new address complete:" + task.isSuccessful());
                if (task.isSuccessful()) {
                    mFirebaseLoadable.setFirebaseData(null);
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.str_Tambah_alamat_gagal), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
