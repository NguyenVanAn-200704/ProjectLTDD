package com.example.ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ui.R;
import com.example.ui.Request.LoginRequest;
import com.example.ui.Retrofit.APIService;
import com.example.ui.Retrofit.RetrofitCilent;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister, textViewForgotPassword;
    private ImageView togglePassword;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mapping();

        buttonLogin.setOnClickListener(v -> {
            if (!isInputValid()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            login();
        });

        textViewRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });

        textViewForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
            finish();
        });

        // Xử lý toggle hiển thị/ẩn mật khẩu
        togglePassword.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                // Hiển thị mật khẩu
                editTextPassword.setTransformationMethod(SingleLineTransformationMethod.getInstance());
                togglePassword.setImageResource(R.drawable.eye_on);
            } else {
                // Ẩn mật khẩu
                editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                togglePassword.setImageResource(R.drawable.eye_off);
            }
            // Di chuyển con trỏ về cuối văn bản
            editTextPassword.setSelection(editTextPassword.getText().length());
        });
    }

    private void mapping() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.textViewRegister);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        togglePassword = findViewById(R.id.toggle_password);
    }

    private boolean isInputValid() {
        return !editTextEmail.getText().toString().isEmpty()
                && !editTextPassword.getText().toString().isEmpty();
    }

    private void login() {
        LoginRequest loginRequest = new LoginRequest(
                editTextEmail.getText().toString(),
                editTextPassword.getText().toString()
        );
        APIService apiService = RetrofitCilent.getRetrofit().create(APIService.class);
        Call<Map<String, Object>> call = apiService.login(loginRequest);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> body = response.body();
                    String message = String.valueOf(body.get("message"));
                    int status = ((Number) body.get("status")).intValue();

                    if (status == 200) {
                        Map<String, Object> userMap = (Map<String, Object>) body.get("user");
                        int userId = ((Number) userMap.get("id")).intValue();
                        String userEmail = (String) userMap.get("email");
                        getSharedPreferences("UserPreferences", MODE_PRIVATE)
                                .edit().putInt("userId", userId).apply();
                        Toast.makeText(LoginActivity.this, "✅ : " + message, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, HomePageActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "❌ : " + message, Toast.LENGTH_SHORT).show();
                    }
                } else if (response.errorBody() != null) {
                    try {
                        String errorJson = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(errorJson);
                        String message = jsonObject.optString("message", "Đăng nhập thất bại!");
                        Toast.makeText(LoginActivity.this, "❌ : " + message, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "Lỗi 1: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "❌ : Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi 2: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("RetrofitFailure", "Lỗi khi gọi API đăng nhập", t);
            }
        });
    }
}