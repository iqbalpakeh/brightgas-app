package com.pertamina.brightgasdriver.firebase.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Agent {

    private String name;
    private String token;
    private String image;
    private String email;
    private String rating;
    private String telp;

    public Agent() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Agent(String name,
                 String token,
                 String image,
                 String email,
                 String rating,
                 String telp) {

        this.name = name;
        this.token = token;
        this.image = image;
        this.email = email;
        this.rating = rating;
        this.telp = telp;
    }

    @Exclude
    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();

        result.put("name", name);
        result.put("token", token);
        result.put("image", token);
        result.put("email", email);
        result.put("rating", rating);
        result.put("telp", telp);

        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTelp() {
        return telp;
    }

    public void setTelp(String telp) {
        this.telp = telp;
    }

}
