package com.pertamina.brightgasagen.firebase;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public interface FirebaseLoadable {

    void setFirebaseData(ArrayList<DataSnapshot> dataSnapshots);

}
