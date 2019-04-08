package com.progremastudio.apipilot.retrofit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("api/auth/register")
    Call<RegisterResponse> postRegister(@Body RegisterRequest body);

    @POST("api/auth/login")
    Call<LoginResponse> postLogin(@Body LoginRequest body);

}
