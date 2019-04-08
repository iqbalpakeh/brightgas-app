package com.pertamina.brightgas.firebase.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Address {

    public String address;
    public String latitude;
    public String longitude;

    public Address() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Address(String address,
                   String latitude,
                   String longitude) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("address", address);
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        return result;
    }
}
