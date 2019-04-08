package com.pertamina.brightgasagen.firebase.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Agent {

    public String name;
    public String token;
    public String image;
    public String email;
    public String rating;

    public Agent() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Agent(String name,
                 String token,
                 String image,
                 String email,
                 String rating) {

        this.name = name;
        this.token = token;
        this.image = image;
        this.email = email;
        this.rating = rating;
    }

    @Exclude
    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();

        result.put("name", name);
        result.put("token", token);
        result.put("image", token);
        result.put("email", email);
        result.put("rating", rating);

        return result;
    }
}
