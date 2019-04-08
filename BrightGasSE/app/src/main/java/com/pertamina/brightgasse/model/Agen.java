package com.pertamina.brightgasse.model;

public class Agen {

    public String id;
    public String name;
    public String distance;
    public float rating;
    public String telp;

    public Agen(String id, String name, String telp, float rating) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.telp = telp;
    }

    public Agen(String name, float rating) {
        this.name = name;
        this.rating = rating;
    }

    public Agen(String id, String name, float rating, String distance) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getTelp() {
        return telp;
    }

    public void setTelp(String telp) {
        this.telp = telp;
    }
}
