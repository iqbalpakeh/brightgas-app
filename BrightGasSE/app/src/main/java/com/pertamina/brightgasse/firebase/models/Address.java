package com.pertamina.brightgasse.firebase.models;

public class Address {

    public String address;
    public String latitude;
    public String longitude;

    public Address() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
}
