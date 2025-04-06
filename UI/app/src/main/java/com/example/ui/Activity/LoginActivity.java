package com.example.ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
    EditText editTextEmail;
    EditText editTextPassword;
    Button buttonLogin;
    TextView textViewRegister;
    LoginRequest loginRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        mapping();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonLogin.setOnClickListener(v -> {
            if (editTextEmail.getText().toString().isEmpty() ||
                    editTextPassword.getText().toString().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            } else {
                loginRequest = new LoginRequest(editTextEmail.getText().toString(),
                        editTextPassword.getText().toString());
                login();
            }
        });

        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void mapping() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.textViewRegister);
    }

    private void login() {
        APIService apiService = RetrofitCilent.getRetrofit().create(APIService.class);
        Call<Map<String, Object>> stringCall = apiService.login(loginRequest);

        stringCall.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if(response.isSuccessful() && response.body() != null){
                    Map<String, Object> responseBody = response.body();
                    String message = responseBody.get("message").toString();
                    int status = ((Number) responseBody.get("status")).intValue();
                    if(status == 201){
                        Toast.makeText(LoginActivity.this, "LoginActivity: " + message, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "LoginActivity: " + message, Toast.LENGTH_SHORT).show();
                    }
                }
                else if (response.errorBody() != null) {
                    try {
                        String errorJson = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(errorJson);

                        String message = jsonObject.has("message") ? jsonObject.getString("message") : "Đăng nhập thất bại!";
                        Toast.makeText(LoginActivity.this, "⚠ " + message, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}