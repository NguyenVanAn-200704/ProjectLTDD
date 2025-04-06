package com.example.ui.Retrofit;

import com.example.ui.Request.LoginRequest;
import com.example.ui.Request.UserRequest;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface APIService {
    @POST("/user/create")
    Call<Map<String, Object>> userRegister(@Body UserRequest userRequest);

    @POST("/user/login")
    Call<Map<String, Object>> login(@Body LoginRequest loginRequest);
}
