package com.progremastudio.apipilot.retrofit;

public class RegisterRequest {

    String Username;
    String Email;
    String Password;
    String ConfirmPassword;
    String Gender;
    String Dob;
    String Address;
    String Phone;

    public RegisterRequest(String Username,
                           String Email,
                           String Password,
                           String ConfirmPassword,
                           String Gender,
                           String Dob,
                           String Address,
                           String Phone) {

        this.Username = Username;
        this.Email = Email;
        this.Password = Password;
        this.ConfirmPassword = ConfirmPassword;
        this.Gender = Gender;
        this.Dob = Dob;
        this.Address = Address;
        this.Phone = Phone;
    }
}
