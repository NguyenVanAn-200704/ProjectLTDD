package com.example.ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ui.Adapter.ProjectAdapter;
import com.example.ui.Model.Project;
import com.example.ui.R;
import com.example.ui.Retrofit.APIService;
import com.example.ui.Retrofit.RetrofitCilent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private EditText edtFullName, edtEmail;
    private Button btnEdit, btnSave;

    private ImageButton uploadImg, btnHome, btnTask, btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        int userId = getSharedPreferences("UserPreferences", MODE_PRIVATE).getInt("userId", -1);
        if (userId == -1) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Không tìm thấy userId. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
        } else {
            fetchProfileFromAPI(userId);
        }
        anhXa();
        navigation();
    }

    void fetchProfileFromAPI(Integer userId) {
        APIService apiService = RetrofitCilent.getRetrofit().create(APIService.class);
        Call<Map<String, Object>> call = apiService.profile(userId); // gọi đúng API

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> user = (Map<String, Object>) response.body().get("user"); // cast đúng kiểu
                    String name = (String) user.get("name");
                    String email = (String) user.get("email");
                    String avatar = (String) user.get("avatar");

                    // Gán giá trị vào EditText
                    edtFullName.setText(name);
                    edtEmail.setText(email);
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    void anhXa() {
        btnHome = findViewById(R.id.btnHome);
        btnTask = findViewById(R.id.btnTask);
        btnProfile = findViewById(R.id.btnProfile);
        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        btnEdit = findViewById(R.id.buttonAddProject);
        btnSave = findViewById(R.id.btnSave);
        uploadImg = findViewById(R.id.btnUploadImg);

    }

    void navigation() {
        btnEdit.setOnClickListener(v -> {
            // Cho phép chỉnh sửa
            edtFullName.setEnabled(true);
            edtEmail.setEnabled(true);
            uploadImg.setVisibility(View.VISIBLE);

            // Hiện nút Save, ẩn nút Edit
            btnSave.setVisibility(View.VISIBLE);
            btnEdit.setVisibility(View.GONE);
        });

        btnSave.setOnClickListener(v -> {
            // Tắt chế độ chỉnh sửa
            edtFullName.setEnabled(false);
            edtEmail.setEnabled(false);

            // Ẩn nút Save, hiện lại nút Edit
            uploadImg.setVisibility(View.GONE);
            btnSave.setVisibility(View.GONE);
            btnEdit.setVisibility(View.VISIBLE);

        });

        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(this, HomePageActivity.class));
            overridePendingTransition(0, 0);
        });

        btnTask.setOnClickListener(v -> {
            startActivity(new Intent(this, TaskPageActivity.class));
            overridePendingTransition(0, 0);
        });

        btnProfile.setOnClickListener(v -> {
            //
        });
    }
}