package com.pertamina.brightgas.firebase.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Driver {

    public String name;
    public String address;
    public String phone;

    public Driver() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Driver(String name,
                 String address,
                 String phone) {

        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("address", address);
        result.put("phone", phone);
        return result;
    }

}
