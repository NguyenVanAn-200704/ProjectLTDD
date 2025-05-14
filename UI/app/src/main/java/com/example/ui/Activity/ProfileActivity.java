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

import com.bumptech.glide.Glide;
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
    private ImageButton uploadImg, btnHome, btnTask, btnProfile, btnLogout;
    private ImageView imageView;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    private boolean isUploading = false;

    private String avatarUrl = null;


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
        btnLogout.setOnClickListener(v -> {
            // Xóa userId khỏi SharedPreferences
            getSharedPreferences("UserPreferences", MODE_PRIVATE)
                    .edit()
                    .remove("userId")
                    .apply();

            // Chuyển về màn hình đăng nhập
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xoá toàn bộ back stack
            startActivity(intent);

            // Hiển thị thông báo
            Toast.makeText(ProfileActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();

            // Kết thúc activity hiện tại
            finish();
        });


    }


    void anhXa() {
        btnLogout = findViewById(R.id.btnLogout);
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
            if (isUploading) {
                Toast.makeText(this, "⏳ Vui lòng chờ ảnh tải lên hoàn tất...", Toast.LENGTH_SHORT).show();
                return;
            }
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
        isUploading = true; // bắt đầu upload
        new UploadAvatar(this, uri, url -> {
            avatarUrl = url;
            Log.d("UploadedAvatar", avatarUrl);
            isUploading = false; // upload xong
            Toast.makeText(ProfileActivity.this, "Ảnh đã tải lên thành công!", Toast.LENGTH_SHORT).show();
        }).execute();
    }

    void updateUser() {
        int userId = getSharedPreferences("UserPreferences", MODE_PRIVATE).getInt("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy userId. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullName = edtFullName.getText().toString();
        if (fullName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập họ tên!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Nếu không có ảnh mới, sử dụng ảnh hiện tại từ profile (nếu có)
        String finalAvatarUrl = avatarUrl != null ? avatarUrl : (String) imageView.getTag(); // Lưu avatar từ API vào tag khi fetch profile
        if (finalAvatarUrl == null) {
            Toast.makeText(this, "Vui lòng chọn ảnh đại diện!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Vô hiệu hóa nút Save để ngăn nhấn nhiều lần
        btnSave.setEnabled(false);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest(userId, fullName, finalAvatarUrl);
        APIService apiService = RetrofitCilent.getRetrofit().create(APIService.class);
        Call<Map<String, Object>> call = apiService.updateUser(updateUserRequest);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                btnSave.setEnabled(true); // Bật lại nút Save
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().get("message").toString();
                    Toast.makeText(ProfileActivity.this, "✅ " + message, Toast.LENGTH_SHORT).show();
                    // Cập nhật giao diện chỉ khi thành công
                    edtFullName.setEnabled(false);
                    uploadImg.setVisibility(View.GONE);
                    btnSave.setVisibility(View.GONE);
                    btnEdit.setVisibility(View.VISIBLE);
                } else {
                    String errorMsg = response.errorBody() != null ? response.errorBody().toString() : "Cập nhật thất bại!";
                    Toast.makeText(ProfileActivity.this, "❌ " + errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                btnSave.setEnabled(true); // Bật lại nút Save
                Toast.makeText(ProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Cập nhật fetchProfileFromAPI để lưu avatar URL
    void fetchProfileFromAPI(Integer userId) {
        APIService apiService = RetrofitCilent.getRetrofit().create(APIService.class);
        Call<Map<String, Object>> call = apiService.profile(userId);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> user = (Map<String, Object>) response.body().get("user");
                    String name = (String) user.get("name");
                    String email = (String) user.get("email");
                    String avatar = (String) user.get("avatar");
                    Glide.with(ProfileActivity.this).load(avatar).into(imageView);
                    imageView.setTag(avatar); // Lưu avatar URL để tái sử dụng
                    edtFullName.setText(name);
                    edtEmail.setText(email);
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Lỗi khi tải hồ sơ: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}