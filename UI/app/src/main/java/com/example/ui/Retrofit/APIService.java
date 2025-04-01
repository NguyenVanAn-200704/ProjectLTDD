package com.example.ui.Retrofit;

import com.example.ui.Request.UserRequest;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIService {
    @POST("/user")
    Call<Map<String, Object>> userRegister(@Body UserRequest userRequest);
}
