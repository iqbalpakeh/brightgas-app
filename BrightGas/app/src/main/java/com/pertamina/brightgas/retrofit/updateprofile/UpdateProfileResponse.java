package com.pertamina.brightgas.retrofit.updateprofile;

import com.google.gson.annotations.SerializedName;

public class UpdateProfileResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Data data;

    public static class Data {

        @SerializedName("username")
        private String username;

        @SerializedName("email")
        private String email;

        @SerializedName("firstname")
        private String firstname;

        @SerializedName("lastname")
        private String lastname;

        @SerializedName("roles")
        private String roles;

        @SerializedName("phone")
        private String phone;

        @SerializedName("address")
        private String address;

        @SerializedName("postalCode")
        private String postalCode;

        @SerializedName("regionID")
        private String regionID;

        @SerializedName("kelurahanID")
        private String kelurahanID;

        @SerializedName("cityID")
        private String cityID;

        @SerializedName("provinceID")
        private String provinceID;

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }

        public String getFirstname() {
            return firstname;
        }

        public String getLastname() {
            return lastname;
        }

        public String getRoles() {
            return roles;
        }

        public String getPhone() {
            return phone;
        }

        public String getAddress() {
            return address;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public String getRegionID() {
            return regionID;
        }

        public String getKelurahanID() {
            return kelurahanID;
        }

        public String getCityID() {
            return cityID;
        }

        public String getProvinceID() {
            return provinceID;
        }
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }
}
