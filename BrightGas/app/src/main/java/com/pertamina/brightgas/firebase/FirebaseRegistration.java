package com.pertamina.brightgas.firebase;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pertamina.brightgas.BaseActivity;
import com.pertamina.brightgas.R;
import com.pertamina.brightgas.firebase.models.Address;
import com.pertamina.brightgas.firebase.models.Customer;

import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class FirebaseRegistration extends FirebaseLoader {

    private static final String TAG = "firebase_registration";

    public static final String NO_URL = "no_url";

    private final int POS_NAME = 0;
    private final int POS_GENDER = 1;
    private final int POS_BIRTHDATE = 2;
    private final int POS_PHONE = 3;
    private final int POS_ADDRESS = 4;
    private final int POS_LATITUDE = 5;
    private final int POS_LONGITUDE = 6;
    private final int POS_EMAIL = 7;
    private final int POS_PASSWORD = 8;

    private BasicNameValuePair[] mInputs;
    private Bitmap mBitmap;
    private String mPictureUri;

    public FirebaseRegistration(Context context, FirebaseLoadable firebaseLoadable) {
        super();
        mFirebaseLoadable = firebaseLoadable;
        mContext = context;
        mPictureUri = NO_URL;
    }

    public void registration(final BasicNameValuePair[] inputs, Bitmap bitmap) {
        mInputs = inputs;
        mBitmap = bitmap;
        mAuth.createUserWithEmailAndPassword(mInputs[POS_EMAIL].getValue(), mInputs[POS_PASSWORD].getValue())
                .addOnCompleteListener((BaseActivity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            createNewUser(task.getResult().getUser());
                        } else {
                            Toast.makeText(mContext, mContext.getString(R.string.str_Registrasi_gagal), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createNewUser(final FirebaseUser firebaseUser) {

        if(mBitmap != null) {
            String uid = firebaseUser.getUid();
            String key = mDatabase.child(PATH_ROOT_PICTURE).child(uid).push().getKey();

            StorageReference pictureReference = mStorage.getReference()
                    .child(PATH_ROOT_PICTURE).child(uid).child(key).child("profile_picture.jpg");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] data = stream.toByteArray();

            UploadTask uploadTask = pictureReference.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d(TAG, "Upload fail = " + exception.toString());
                }

            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mPictureUri = taskSnapshot.getDownloadUrl().toString();
                    Log.d(TAG, "Download Uri " + mPictureUri);
                    createNewUser();
                }
            });

        } else {
            createNewUser();
        }
    }

    private void createNewUser() {

        String uid = mAuth.getCurrentUser().getUid();
        String key = mDatabase.child(PATH_ROOT_ADDRESS + "_" + uid).push().getKey();

        Customer customer = new Customer(
                mInputs[POS_NAME].getValue(),
                mInputs[POS_EMAIL].getValue(),
                mInputs[POS_GENDER].getValue(),
                mInputs[POS_PHONE].getValue(),
                mInputs[POS_BIRTHDATE].getValue(),
                String.valueOf(Calendar.getInstance().getTimeInMillis()),
                mPictureUri,
                uid
        );
        mDatabase.child(PATH_ROOT_USERS).child(uid).setValue(customer.toMap());

        Address address = new Address(
                mInputs[POS_ADDRESS].getValue(),
                mInputs[POS_LATITUDE].getValue(),
                mInputs[POS_LONGITUDE].getValue()
        );
        mDatabase.child(PATH_ROOT_ADDRESS).child(uid).child(key).setValue(address.toMap());

        queryUserCredential(uid);
    }

    private void queryUserCredential(String uid) {

        mDatabase.child(PATH_ROOT_USERS).child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
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
                    }
                });
    }

}
