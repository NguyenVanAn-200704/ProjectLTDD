package com.example.ui.Retrofit;

import com.example.ui.Model.Member;
import com.example.ui.Request.CreateProjectRequest;
import com.example.ui.Request.EmailOTPRequest;
import com.example.ui.Request.LoginRequest;
import com.example.ui.Request.TaskRequest;
import com.example.ui.Request.UpdateProjectMemberRequest;
import com.example.ui.Request.UpdateTaskRequest;
import com.example.ui.Request.UpdateUserRequest;
import com.example.ui.Request.UserRequest;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {
    @POST("/user/create")
    Call<Map<String, Object>> userRegister(@Body UserRequest userRequest);

    @POST("/user/login")
    Call<Map<String, Object>> login(@Body LoginRequest loginRequest);

    @POST("/user/send-otp")
    Call<Map<String, Object>> sendOTP(@Query("email") String email);

    @POST("/user/verify-otp")
    Call<Map<String, Object>> verifyOTP(@Body EmailOTPRequest emailOTPRequest);

    @POST("/user/reset-password")
    Call<Map<String, Object>> resetPassword(@Body EmailOTPRequest emailOTPRequest);

    @POST("/project/create")
    Call<Map<String, Object>> createProject(@Body CreateProjectRequest createProjectRequest);

    @PUT("/user/update")
    Call<Map<String, Object>> updateUser(@Body UpdateUserRequest updateUserRequest);

    @GET("/project/all")
    Call<Map<String, Object>> allProjects(@Query("id") Integer userId);

    @GET("/project/task/all")
    Call<Map<String, Object>> allTasksInProject(@Query("id") Integer projectId);

    @GET("/user/task/all")
    Call<Map<String, Object>> allTasksInUser(@Query("id") Integer userId);

    @GET("/user/profile")
    Call<Map<String, Object>> profile(@Query("id") Integer userId);

    @GET("/user/check")
    Call<Map<String, Object>> checkUserByEmail(@Query("email") String email);

    @POST("/project/member/add")
    Call<Map<String, Object>> addMember(@Body Member member);

    @GET("/project/{projectId}/members")
    Call<Map<String, Object>> getAllMember(@Path("projectId") int projectId);

    // Thêm API update member
    @PUT("/project/member/update")
    Call<Map<String, Object>> updateMember(@Body UpdateProjectMemberRequest updateMemberRequest);

    // Thêm API delete member
    @DELETE("/project/member/delete")
    Call<Map<String, Object>> deleteMember(@Query("id") Integer id);

    @POST("/project/task/add")
    Call<Map<String, Object>> addTask(@Body TaskRequest taskRequest);

    @PUT("/project/task/update")
    Call<Map<String, Object>> updateTask(@Body UpdateTaskRequest updateTaskRequest);

    @GET("/project/task/get")
    Call<Map<String,Object>> getTaskById(@Query("id") int id);
    @DELETE("/project/delete")
    Call<Map<String, Object>> deleteProject(@Query("id") Integer id);

    @GET("/project/member/role")
    Call<Map<String, Object>> getUserRole(@Query("projectId") Integer projectId,@Query("userId") Integer userId);
}
