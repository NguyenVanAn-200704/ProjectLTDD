package com.example.ui.Activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ui.R;
import com.example.ui.Request.UserRequest;
import com.example.ui.Retrofit.APIService;
import com.example.ui.Retrofit.RetrofitCilent;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    EditText editTextEmail;
    EditText editTextPassword;
    EditText editTextRePassword;
    EditText editTextName;
    Button buttonRegister;
    UserRequest userRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        mapping();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonRegister.setOnClickListener(v -> {
            if (editTextPassword.getText().toString().equals(editTextRePassword.getText().toString())) {
                if (editTextEmail.getText().toString().isEmpty() ||
                        editTextPassword.getText().toString().isEmpty() ||
                        editTextRePassword.getText().toString().isEmpty() ||
                        editTextName.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }
                userRequest = new UserRequest(editTextEmail.getText().toString(),
                        editTextPassword.getText().toString(),
                        editTextName.getText().toString(),
                        "abcxyz");
                register();
            } else {
                Toast.makeText(RegisterActivity.this, "Mật khẩu không trùng khớp !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mapping() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextRePassword = findViewById(R.id.editTextRePassword);
        editTextName = findViewById(R.id.editTextName);
        buttonRegister = findViewById(R.id.buttonRegister);
    }

    private void register() {
        APIService apiService = RetrofitCilent.getRetrofit().create(APIService.class);
        Call<Map<String, Object>> stringCall = apiService.userRegister(userRequest);

        stringCall.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Lấy thông tin từ Map trả về
                    Map<String, Object> responseBody = response.body();

                    // Lấy message từ response
                    String message = responseBody.get("message").toString();
                    int status = ((Number) responseBody.get("status")).intValue(); // Chuyển về kiểu số

                    // Hiển thị thông báo theo trạng thái
                    if (status == 201) {
                        Toast.makeText(RegisterActivity.this, "✅ " + message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "⚠ " + message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}