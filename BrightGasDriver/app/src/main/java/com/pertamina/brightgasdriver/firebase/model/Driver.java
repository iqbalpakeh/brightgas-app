package com.pertamina.brightgasdriver.firebase.model;

public class Driver {

    private String name;
    private String token;
    private String image;
    private String email;
    private String rating;
    private String telp;
    private String latitude;
    private String longitude;
    private String agenId;

    public Driver() {
        // Default constructor required for calls to DataSnapshot.getValue(Driver.class)
    }

    public Driver(String name, String token, String image, String email,
                  String rating, String telp, String latitude, String longitude, String agenId) {

        this.name = name;
        this.token = token;
        this.image = image;
        this.email = email;
        this.rating = rating;
        this.telp = telp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.agenId = agenId;
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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAgenId() {
        return agenId;
    }

    public void setAgenId(String agenId) {
        this.agenId = agenId;
    }
}
