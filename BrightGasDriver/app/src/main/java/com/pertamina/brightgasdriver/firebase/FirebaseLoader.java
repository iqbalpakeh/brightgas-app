package com.pertamina.brightgasdriver.firebase;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class FirebaseLoader {

    static final String TAG = "firebase_loader";

    static final String PATH_ROOT_SALES = "sales";
    static final String PATH_ROOT_USERS = "users";
    static final String PATH_ROOT_ADDRESS = "address";
    static final String PATH_ROOT_ORDERS = "orders";
    static final String PATH_ROOT_AGENTS = "agents";
    static final String PATH_ROOT_DRIVERS = "drivers";
    static final String PATH_ROOT_PICTURE = "pictures";
    static final String PATH_ROOT_PRODUCT = "product";

    FirebaseLoadable mFirebaseLoadable;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    FirebaseStorage mStorage;

    Context mContext;
    ArrayList<DataSnapshot> mDataSnapshotList;

    FirebaseLoader() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance();
    }

}
