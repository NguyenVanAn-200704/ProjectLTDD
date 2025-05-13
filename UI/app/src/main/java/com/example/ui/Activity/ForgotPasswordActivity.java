package com.example.ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ui.R;
import com.example.ui.Request.EmailOTPRequest;
import com.example.ui.Retrofit.APIService;
import com.example.ui.Retrofit.RetrofitCilent;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword, editTextOTP;
    Button buttonSendOTP, buttonConfirm;
    ImageView togglePassword;
    TextView textViewBackToLogin;
    boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        mapping();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        togglePassword.setOnClickListener(v -> {
//            if (isPasswordVisible) {
//                editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                togglePassword.setImageResource(R.drawable.eye_off);
//            } else {
//                editTextPassword.setTransformationMethod(SingleLineTransformationMethod.getInstance());
//                togglePassword.setImageResource(R.drawable.eye_on);
//            }
//            isPasswordVisible = !isPasswordVisible;
//            editTextPassword.setSelection(editTextPassword.length());
//        });

        buttonSendOTP.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
            } else {
                sendOTP(email);
            }
        });

        buttonConfirm.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String newPassword = editTextPassword.getText().toString().trim();
            String otp = editTextOTP.getText().toString().trim();

            if (email.isEmpty() || newPassword.isEmpty() || otp.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            } else {
                confirmReset(email, newPassword, otp);
            }
        });

        // ⬅️ Xử lý nhấn "Quay về đăng nhập"
        textViewBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void mapping() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextOTP = findViewById(R.id.editTextOTP);
        buttonSendOTP = findViewById(R.id.buttonSendOTP);
        buttonConfirm = findViewById(R.id.buttonConfirm);
        togglePassword = findViewById(R.id.toggle_password);
        textViewBackToLogin = findViewById(R.id.textViewBackToLogin); // Ánh xạ TextView mới
    }

    private void sendOTP(String email) {
        APIService apiService = RetrofitCilent.getRetrofit().create(APIService.class);
        Call<Map<String, Object>> call = apiService.sendOTP(email);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().get("message").toString();
                    Toast.makeText(ForgotPasswordActivity.this, "✅ " + message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "❌ Gửi OTP thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, "❌ Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmReset(String email, String newPassword, String otp) {
        APIService apiService = RetrofitCilent.getRetrofit().create(APIService.class);
        EmailOTPRequest request = new EmailOTPRequest(email, newPassword, otp);
        Call<Map<String, Object>> call = apiService.resetPassword(request);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().get("message").toString();
                    Toast.makeText(ForgotPasswordActivity.this, "✅ " + message, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        String errorJson = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(errorJson);
                        String message = jsonObject.getString("message");
                        Toast.makeText(ForgotPasswordActivity.this, "❌ " + message, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(ForgotPasswordActivity.this, "❌ Lỗi xử lý: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, "❌ Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
