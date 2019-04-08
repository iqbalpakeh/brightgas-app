package com.pertamina.brightgasdriver.firebase;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public interface FirebaseLoadable {
    void setFirebaseData(ArrayList<DataSnapshot> dataSnapshots);
}
