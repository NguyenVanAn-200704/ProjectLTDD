package com.example.ui.Retrofit;

import com.example.ui.Request.CreateProjectRequest;
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

    @POST("/project/create")
    Call<Map<String, Object>> createProject(@Body CreateProjectRequest createProjectRequest);

    @GET("/project/all")
    Call<Map<String, Object>> allProjects(@retrofit2.http.Query("id") Integer userId);

    @GET("/project/task/all")
    Call<Map<String, Object>> allTasksInProject(@retrofit2.http.Query("id") Integer projectId);

    @GET("/user/task/all")
    Call<Map<String, Object>> allTasksInUser(@retrofit2.http.Query("id") Integer userId);

    @GET("/user/profile")
    Call<Map<String, Object>> profile(@retrofit2.http.Query("id") Integer userId);
}
