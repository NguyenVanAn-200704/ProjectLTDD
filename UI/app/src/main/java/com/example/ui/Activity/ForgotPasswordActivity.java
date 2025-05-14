package com.example.ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.view.View;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ui.R;
import com.example.ui.Request.ResetPasswordRequest;
import com.example.ui.Request.VerifyOTPRequest;
import com.example.ui.Retrofit.APIService;
import com.example.ui.Retrofit.RetrofitCilent;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextView textViewBackToLogin, titleText;
    private EditText editTextEmail, editTextOTP, editTextNewPassword, editTextConfirmPassword;
    private ImageView togglePassword;
    private AppCompatButton buttonAction;
    private View emailLayout, otpLayout, passwordLayout;
    private String userEmail;
    private boolean isPasswordVisible = false;
    private int currentStep = 1; // 1: Email, 2: OTP, 3: Password
    private TextView textViewBackToLogin;
    private String userEmail; // Store email for later steps

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        // Initialize views
        textViewBackToLogin = findViewById(R.id.textViewBackToLogin);
        titleText = findViewById(R.id.title_text);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextOTP = findViewById(R.id.editTextOTP);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        togglePassword = findViewById(R.id.toggle_password);
        buttonAction = findViewById(R.id.buttonAction);
        emailLayout = findViewById(R.id.email_layout);
        otpLayout = findViewById(R.id.otp_layout);
        passwordLayout = findViewById(R.id.password_layout);

        // Handle back to login
        textViewBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
            finish();
        });

        // Handle password visibility toggle
        togglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                editTextNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                editTextConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                togglePassword.setImageResource(R.drawable.eye_off);
            } else {
                editTextNewPassword.setTransformationMethod(SingleLineTransformationMethod.getInstance());
                editTextConfirmPassword.setTransformationMethod(SingleLineTransformationMethod.getInstance());
                togglePassword.setImageResource(R.drawable.eye_on);
            }
            isPasswordVisible = !isPasswordVisible;
            editTextNewPassword.setSelection(editTextNewPassword.length());
            editTextConfirmPassword.setSelection(editTextConfirmPassword.length());
        });

        // Handle action button
        buttonAction.setOnClickListener(v -> {
            if (currentStep == 1) {
                String email = editTextEmail.getText().toString().trim();
                if (email.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
                } else {
                    userEmail = email;
                    sendOTP(email);
                }
            } else if (currentStep == 2) {
                String otp = editTextOTP.getText().toString().trim();
                if (otp.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập mã OTP!", Toast.LENGTH_SHORT).show();
                } else {
                    verifyOTP(otp);
                }
            } else if (currentStep == 3) {
                String newPassword = editTextNewPassword.getText().toString().trim();
                String confirmPassword = editTextConfirmPassword.getText().toString().trim();
                if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                } else if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(this, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
                } else {
                    confirmReset(userEmail, newPassword);
                }
            }
        });

        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void updateUIForStep(int step) {
        currentStep = step;
        emailLayout.setVisibility(step == 1 ? View.VISIBLE : View.GONE);
        otpLayout.setVisibility(step == 2 ? View.VISIBLE : View.GONE);
        passwordLayout.setVisibility(step == 3 ? View.VISIBLE : View.GONE);
        titleText.setText(step == 1 ? "Quên mật khẩu" : step == 2 ? "Nhập mã OTP" : "Đặt lại mật khẩu");
        buttonAction.setText(step == 1 ? "Gửi OTP" : step == 2 ? "Xác nhận OTP" : "Lưu mật khẩu");
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
                    updateUIForStep(2);
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Toast.makeText(ForgotPasswordActivity.this, "❌ Gửi OTP thất bại!", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, "❌ Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifyOTP(String otp) {
        APIService apiService = RetrofitCilent.getRetrofit().create(APIService.class);
        Call<Map<String, Object>> call = apiService.verifyOTP(new VerifyOTPRequest(userEmail, otp));

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().get("message").toString();
                    Toast.makeText(ForgotPasswordActivity.this, "✅ " + message, Toast.LENGTH_SHORT).show();
                    updateUIForStep(3);
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

    private void confirmReset(String email, String newPassword) {
        APIService apiService = RetrofitCilent.getRetrofit().create(APIService.class);
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(email, newPassword);
        Call<Map<String, Object>> call = apiService.resetPassword(resetPasswordRequest);

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