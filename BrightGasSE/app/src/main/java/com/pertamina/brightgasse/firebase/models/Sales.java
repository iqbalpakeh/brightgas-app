package com.pertamina.brightgasse.firebase.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Sales {

    public String name;
    public String token;
    public String image;
    public String email;

    public Sales() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Sales(String name,
                 String token,
                 String image,
                 String email) {

        this.name = name;
        this.token = token;
        this.image = image;
        this.email = email;
    }

    @Exclude
    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();

        result.put("name", name);
        result.put("token", token);
        result.put("image", token);
        result.put("email", email);

        return result;
    }

}
