package com.pertamina.brightgas.retrofit.login;

public class LoginRequest {

    String Username;
    String Password;

    public LoginRequest(String Username,
                        String Password) {
        this.Username = Username;
        this.Password = Password;
    }
}
