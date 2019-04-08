package com.pertamina.brightgasagen.firebase.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Address {

    public String address;
    public String city;
    public String province;
    public String cityId;
    public String provinceId;
    public String latitude;
    public String longitude;

    public Address() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Address(String address,
                   String city,
                   String province,
                   String cityId,
                   String provinceId,
                   String latitude,
                   String longitude) {

        this.address = address;
        this.city = city;
        this.province = province;
        this.cityId = cityId;
        this.provinceId = provinceId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("address", address);
        result.put("city", city);
        result.put("province", province);
        result.put("cityId", cityId);
        result.put("provinceId", provinceId);
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        return result;
    }

}
