package com.progremastudio.apipilot.retrofit;

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

    public class Data {

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

    }

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
}
