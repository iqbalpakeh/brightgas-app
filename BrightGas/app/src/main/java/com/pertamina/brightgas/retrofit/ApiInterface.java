package com.pertamina.brightgas.retrofit;

import com.pertamina.brightgas.retrofit.addaddress.AddAddressRequest;
import com.pertamina.brightgas.retrofit.addaddress.AddAddressResponse;
import com.pertamina.brightgas.retrofit.createorder.CreateOrderRequest;
import com.pertamina.brightgas.retrofit.createorder.CreateOrderResponse;
import com.pertamina.brightgas.retrofit.deleteaddress.DeleteAddressResponse;
import com.pertamina.brightgas.retrofit.detailorder.DetailOrderResponse;
import com.pertamina.brightgas.retrofit.listaddress.ListAddressResponse;
import com.pertamina.brightgas.retrofit.listorder.ListOrderResponse;
import com.pertamina.brightgas.retrofit.listproduct.ListProductResponse;
import com.pertamina.brightgas.retrofit.login.LoginRequest;
import com.pertamina.brightgas.retrofit.login.LoginResponse;
import com.pertamina.brightgas.retrofit.register.RegisterRequest;
import com.pertamina.brightgas.retrofit.register.RegisterResponse;
import com.pertamina.brightgas.retrofit.customerprofile.CustomerProfileResponse;
import com.pertamina.brightgas.retrofit.updateprofile.UpdateProfileRequest;
import com.pertamina.brightgas.retrofit.updateprofile.UpdateProfileResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {

    @POST("api/auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest body);

    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest body);

    @POST("api/profile/update-profile")
    Call<UpdateProfileResponse> updateProfile(@Header("Authorization") String token, @Body UpdateProfileRequest body);

    @POST("/api/address/add")
    Call<AddAddressResponse> addAddress(@Header("Authorization") String token, @Body AddAddressRequest body);

    @POST("/api/order")
    Call<CreateOrderResponse> createOrder(@Header("Authorization") String token, @Body CreateOrderRequest body);

    @GET("/api/customer/profile")
    Call<CustomerProfileResponse> customerProfile(@Header("Authorization") String token);

    @GET("api/address?page=1&limit=50")
    Call<ListAddressResponse> listAddress(@Header("Authorization") String token);

    @GET("/api/product?page=1&limit=20")
    Call<ListProductResponse> listProduct(@Header("Authorization") String token);

    @GET("/api/order/customer")
    Call<ListOrderResponse> listOrder(@Header("Authorization") String token);

    @GET("/api/order/{orderId}")
    Call<DetailOrderResponse> showDetailOder(@Header("Authorization") String token, @Path("orderId") String orderId);

    @DELETE("/api/address/{addressId}")
    Call<DeleteAddressResponse> deleteAddress(@Header("Authorization") String token, @Path("addressId") String addressId);

}
