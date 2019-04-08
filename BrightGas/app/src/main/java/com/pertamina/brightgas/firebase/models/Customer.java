package com.pertamina.brightgas.firebase.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Customer {

    public final static String MALE = "Pria";
    public final static String FEMALE = "Wanita";

    public String name;
    public String email;
    public String gender;
    public String phoneNumber;
    public String birthdate;
    public String timestamp;
    public String pictureUrl;
    public String uid;

    public Customer() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Customer(String name,
                    String email,
                    String gender,
                    String phoneNumber,
                    String birthdate,
                    String timestamp,
                    String pictureUrl,
                    String uid) {

        this.name = name;
        this.email = email;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.birthdate = birthdate;
        this.timestamp = timestamp;
        this.pictureUrl = pictureUrl;
        this.uid = uid;
    }

    @Exclude
    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();

        result.put("name", name);
        result.put("email", email);
        result.put("gender", gender);
        result.put("phoneNumber", phoneNumber);
        result.put("birthdate", birthdate);
        result.put("timestamp", timestamp);
        result.put("pictureUrl", pictureUrl);
        result.put("uid", uid);

        return result;
    }

}
