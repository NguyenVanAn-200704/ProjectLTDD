package com.example.ui.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ui.Adapter.ProjectAdapter;
import com.example.ui.Model.Project;
import com.example.ui.R;
import com.example.ui.Request.UpdateUserRequest;
import com.example.ui.Retrofit.APIService;
import com.example.ui.Retrofit.RetrofitCilent;
import com.example.ui.Upload.UploadAvatar;

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
    private ImageView imageView;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        anhXa();
        navigation();
        int userId = getSharedPreferences("UserPreferences", MODE_PRIVATE).getInt("userId", -1);
        if (userId == -1) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Không tìm thấy userId. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
        } else {
            fetchProfileFromAPI(userId);
        }
        uploadImg.setOnClickListener(v -> chooseImage());
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
        imageView = findViewById(R.id.imgAvatar);

    }

    void navigation() {
        btnEdit.setOnClickListener(v -> {
            edtFullName.setEnabled(true);
            uploadImg.setVisibility(View.VISIBLE);

            btnSave.setVisibility(View.VISIBLE);
            btnEdit.setVisibility(View.GONE);
        });

        btnSave.setOnClickListener(v -> {
            edtFullName.setEnabled(false);
            edtEmail.setEnabled(false);

            uploadImg.setVisibility(View.GONE);
            btnSave.setVisibility(View.GONE);
            btnEdit.setVisibility(View.VISIBLE);

            updateUser();
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

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri); // Hiển thị trước ảnh
            uploadImageToCloudinary(imageUri); // Upload ảnh
        }
    }

    private void uploadImageToCloudinary(Uri uri) {
        new UploadAvatar(this, uri, avatarUrl -> {
            // Sau khi upload thành công, avatarUrl là link ảnh trên cloudinary
            Log.d("UploadedAvatar", avatarUrl);
//            updateAvatarToServer(avatarUrl); // Gọi API cập nhật avatar cho user
        }).execute();
    }

    void updateUser() {
        int userId = getSharedPreferences("UserPreferences", MODE_PRIVATE).getInt("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy userId. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullName = edtFullName.getText().toString();
        String email = edtEmail.getText().toString();

        if (fullName.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        UpdateUserRequest updateUserRequest = new UpdateUserRequest(userId, fullName, email);
        APIService apiService = RetrofitCilent.getRetrofit().create(APIService.class);
        Call<Map<String, Object>> call = apiService.updateUser(updateUserRequest);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().get("message").toString();
                    Toast.makeText(ProfileActivity.this, "✅ " + message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "❌ Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}