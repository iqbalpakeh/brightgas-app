package com.pertamina.brightgas.retrofit.register;

import com.pertamina.brightgas.retrofit.login.LoginResponse;

public interface RegisterInterface {
    void retrofitRegister(RegisterResponse registerResponse, LoginResponse loginResponse);
}
