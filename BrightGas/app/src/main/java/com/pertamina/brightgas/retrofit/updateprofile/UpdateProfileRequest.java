package com.pertamina.brightgas.retrofit.updateprofile;

public class UpdateProfileRequest {

    String Username;
    String Email;
    String Firstname;
    String Lastname;
    String Address;
    String Phone;
    String PostalCode;
    String KelurahanID;
    String RegionID;
    String CityID;
    String ProvinceID;

    public UpdateProfileRequest(String username,
                                String email,
                                String firstname,
                                String lastname,
                                String address,
                                String phone,
                                String postalCode,
                                String kelurahanID,
                                String regionID,
                                String cityID,
                                String provinceID) {
        Username = username;
        Email = email;
        Firstname = firstname;
        Lastname = lastname;
        Address = address;
        Phone = phone;
        PostalCode = postalCode;
        KelurahanID = kelurahanID;
        RegionID = regionID;
        CityID = cityID;
        ProvinceID = provinceID;
    }
}
