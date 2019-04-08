package com.pertamina.brightgas.retrofit.register;

public class RegisterRequest {

    private String Email;
    private String Name;
    private String Phone;
    private String Password;
    private String ConfirmPassword;
    private String Address;
    private String Latitude;
    private String Longitude;
    private String Gender;
    private String Dob;

    public RegisterRequest(String email,
                           String name,
                           String phone,
                           String password,
                           String confirmPassword,
                           String address,
                           String latitude,
                           String longitude,
                           String gender,
                           String dob) {
        Email = email;
        Name = name;
        Phone = phone;
        Password = password;
        ConfirmPassword = confirmPassword;
        Address = address;
        Latitude = latitude;
        Longitude = longitude;
        Gender = gender;
        Dob = dob;
    }

    public String getEmail() {
        return Email;
    }

    public String getName() {
        return Name;
    }

    public String getPhone() {
        return Phone;
    }

    public String getPassword() {
        return Password;
    }

    public String getConfirmPassword() {
        return ConfirmPassword;
    }

    public String getAddress() {
        return Address;
    }

    public String getLatitude() {
        return Latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public String getGender() {
        return Gender;
    }

    public String getDob() {
        return Dob;
    }

}
