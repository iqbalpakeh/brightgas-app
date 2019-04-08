package com.pertamina.brightgas.retrofit.login;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("statusCode")
    private String statusCode;

    @SerializedName("token")
    private String token;

    @SerializedName("expiration")
    private String expiration;

    @SerializedName("data")
    private Data data;

    public String getStatusCode() {
        return statusCode;
    }

    public String getToken() {
        return token;
    }

    public String getExpiration() {
        return expiration;
    }

    public Data getData() {
        return data;
    }

    public static class Data {

        @SerializedName("name")
        private String name;

        @SerializedName("username")
        private String username;

        @SerializedName("email")
        private String email;

        @SerializedName("roles")
        private String roles;

        public String getName() {
            return name;
        }

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }

        public String getRoles() {
            return roles;
        }
    }
}
