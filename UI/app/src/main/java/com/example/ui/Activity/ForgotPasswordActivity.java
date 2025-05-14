package com.example.ui.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;
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

    private TextView textViewBackToLogin;
    private String userEmail; // Store email for later steps

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        textViewBackToLogin = findViewById(R.id.textViewBackToLogin);

        // Show first dialog for email input
        showEmailDialog();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textViewBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void showEmailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nhập Email");

        // Set up the input
        final EditText input = new EditText(this);
        input.setHint("Email");
        input.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

        builder.setPositiveButton("Gửi OTP", null); // Set null initially, we'll override
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Override positive button to prevent auto-dismiss
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String email = input.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
            } else {
                userEmail = email;
                sendOTP(email, dialog);
            }
        });
    }

    private void showOTPDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nhập mã OTP");

        final EditText input = new EditText(this);
        input.setHint("Mã OTP");
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Xác nhận", null);
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String otp = input.getText().toString().trim();
            if (otp.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mã OTP!", Toast.LENGTH_SHORT).show();
            } else {
                verifyOTP(otp, dialog);
            }
        });
    }

    private void showNewPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Đặt lại mật khẩu");

        // Create a LinearLayout to hold both password fields and toggle
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final EditText passwordInput = new EditText(this);
        passwordInput.setHint("Mật khẩu mới");
        passwordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(passwordInput);

        final EditText confirmPasswordInput = new EditText(this);
        confirmPasswordInput.setHint("Xác nhận mật khẩu");
        confirmPasswordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(confirmPasswordInput);

        final ImageView togglePassword = new ImageView(this);
        togglePassword.setImageResource(R.drawable.eye_off);
        togglePassword.setPadding(4, 4, 4, 4);
        boolean[] isPasswordVisible = {false};
        togglePassword.setOnClickListener(v -> {
            if (isPasswordVisible[0]) {
                passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                confirmPasswordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                togglePassword.setImageResource(R.drawable.eye_off);
            } else {
                passwordInput.setTransformationMethod(SingleLineTransformationMethod.getInstance());
                confirmPasswordInput.setTransformationMethod(SingleLineTransformationMethod.getInstance());
                togglePassword.setImageResource(R.drawable.eye_on);
            }
            isPasswordVisible[0] = !isPasswordVisible[0];
            passwordInput.setSelection(passwordInput.length());
            confirmPasswordInput.setSelection(confirmPasswordInput.length());
        });
        layout.addView(togglePassword);

        builder.setView(layout);

        builder.setPositiveButton("Lưu", null);
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String newPassword = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            } else if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
            } else {
                confirmReset(userEmail, newPassword, dialog);
            }
        });
    }

    private void sendOTP(String email, AlertDialog dialog) {
        APIService apiService = RetrofitCilent.getRetrofit().create(APIService.class);
        Call<Map<String, Object>> call = apiService.sendOTP(email);

        Log.d("API_DEBUG", "Sending OTP to: " + email);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API_DEBUG", "Response: " + response.body());
                    String message = response.body().get("message").toString();
                    Toast.makeText(ForgotPasswordActivity.this, "✅ " + message, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    showOTPDialog();
                } else {
                    Log.e("API_DEBUG", "Error response: " + response.code() + " - " + response.message());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e("API_DEBUG", "Error body: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(ForgotPasswordActivity.this, "❌ Gửi OTP thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e("API_DEBUG", "Failed to send OTP: " + t.getMessage(), t);
                Toast.makeText(ForgotPasswordActivity.this, "❌ Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifyOTP(String otp, AlertDialog dialog) {
        // Assuming you have an API endpoint to verify OTP
        APIService apiService = RetrofitCilent.getRetrofit().create(APIService.class);
        Call<Map<String, Object>> call = apiService.verifyOTP(new VerifyOTPRequest(userEmail, otp));

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().get("message").toString();
                    Toast.makeText(ForgotPasswordActivity.this, "✅ " + message, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    showNewPasswordDialog();
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

    private void confirmReset(String email, String newPassword, AlertDialog dialog) {
        APIService apiService = RetrofitCilent.getRetrofit().create(APIService.class);
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(email, newPassword);
        Call<Map<String, Object>> call = apiService.resetPassword(resetPasswordRequest);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().get("message").toString();
                    Toast.makeText(ForgotPasswordActivity.this, "✅ " + message, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
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