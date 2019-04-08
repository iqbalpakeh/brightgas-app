package com.pertamina.brightgasse.firebase;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public interface FirebaseLoadable {

    void setFirebaseData(ArrayList<DataSnapshot> dataSnapshots);

}
