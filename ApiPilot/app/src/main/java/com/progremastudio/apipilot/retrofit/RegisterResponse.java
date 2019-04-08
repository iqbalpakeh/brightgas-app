package com.progremastudio.apipilot.retrofit;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

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
