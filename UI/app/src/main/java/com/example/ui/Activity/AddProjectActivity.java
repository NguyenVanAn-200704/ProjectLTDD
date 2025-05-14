package com.example.ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ui.Adapter.MemberAdapter;
import com.example.ui.Model.Member;
import com.example.ui.R;
import com.example.ui.Request.CreateProjectRequest;
import com.example.ui.Retrofit.APIService;
import com.example.ui.Retrofit.RetrofitCilent;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProjectActivity extends AppCompatActivity implements MemberAdapter.OnMemberActionListener{
    TextInputEditText editTextName;
    TextInputEditText editTextDescription;
    Button buttonAddProject;
    Button btnAddMember;
    CreateProjectRequest createProjectRequest;
    List<Member> memberList = new ArrayList<>();
    MemberAdapter memberAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_project);
        mapping();

        // Initialize member adapter with context
        memberAdapter = new MemberAdapter(this, memberList, this);
        buttonAddProject.setOnClickListener(v -> {
            if (editTextName.getText().toString().isEmpty() ||
                    editTextDescription.getText().toString().isEmpty()) {
                Toast.makeText(AddProjectActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            } else {
                int userId = getSharedPreferences("UserPreferences", MODE_PRIVATE).getInt("userId", -1);
                if (userId == -1) {
                    Intent intent = new Intent(AddProjectActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(this, "Không tìm thấy userId. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
                } else {
                    createProjectRequest = new CreateProjectRequest(
                            userId,
                            editTextName.getText().toString(),
                            editTextDescription.getText().toString(),
                            memberList);
                    create();
                }
            }
        });

        btnAddMember.setOnClickListener(v -> showAddMemberDialog());

        navigation();
    }

    private void mapping() {
        editTextName = findViewById(R.id.editTextName);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonAddProject = findViewById(R.id.buttonAddProject);
        btnAddMember = findViewById(R.id.btnAddMember);
    }

    private void showAddMemberDialog() {

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_member, null);
        dialog.setContentView(dialogView);
        View parentLayout = dialogView.getParent() instanceof View ? (View) dialogView.getParent() : null;
        if (parentLayout != null) {
            parentLayout.getLayoutParams().height = (int) (getResources().getDisplayMetrics().heightPixels * 0.85); // Chiếm 85% chiều cao màn hình
        }


        TextInputEditText edtEmail = dialogView.findViewById(R.id.edtEmail);
        TextInputLayout tilEmail = dialogView.findViewById(R.id.tilEmail);
        Spinner spinnerRole = dialogView.findViewById(R.id.spinnerRole);
        Button btnAddMemberDialog = dialogView.findViewById(R.id.btnAddMember);
        RecyclerView recyclerMembers = dialogView.findViewById(R.id.recyclerMembers);
        ProgressBar progressBar = dialogView.findViewById(R.id.progressBar);

        // Setup Spinner
        List<String> roles = Arrays.asList("Admin", "Member", "Viewer");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(spinnerAdapter);

        // Setup RecyclerView
        recyclerMembers.setLayoutManager(new LinearLayoutManager(this));
        recyclerMembers.setAdapter(memberAdapter);

        btnAddMemberDialog.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String role = spinnerRole.getSelectedItem().toString();

            if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
                tilEmail.setError("Vui lòng nhập email hợp lệ!");
                return;
            }

            tilEmail.setError(null);
            progressBar.setVisibility(View.VISIBLE);
            btnAddMemberDialog.setEnabled(false);

            // Gọi API kiểm tra email
            APIService apiService = RetrofitCilent.getRetrofit().create(APIService.class);
            Call<Map<String, Object>> call = apiService.checkUserByEmail(email);
            call.enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    progressBar.setVisibility(View.GONE);
                    btnAddMemberDialog.setEnabled(true);

                    if (response.isSuccessful() && response.body() != null) {
                        Map<String, Object> responseBody = response.body();
                        int status = ((Number) responseBody.get("status")).intValue();
                        if (status == 200) {
                            // Lấy thông tin người dùng từ API
                            Map<String, Object> userData = (Map<String, Object>) responseBody.get("data");
                            String userEmail = userData.get("email").toString();
                            String avatar = userData.get("avatar") != null ? userData.get("avatar").toString() : "";

                            // Thêm thành viên vào danh sách
                            memberList.add(new Member(userEmail, role, avatar));
                            memberAdapter.notifyDataSetChanged();
                            edtEmail.setText("");
                            Toast.makeText(AddProjectActivity.this, "Đã thêm thành viên!", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = responseBody.get("message").toString();
                            tilEmail.setError(message);
                            Toast.makeText(AddProjectActivity.this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        try {
                            String errorJson = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(errorJson);
                            String message = jsonObject.has("message") ? jsonObject.getString("message") : "Email không tồn tại!";
                            tilEmail.setError(message);
                            Toast.makeText(AddProjectActivity.this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            tilEmail.setError("Lỗi kiểm tra email!");
                            Toast.makeText(AddProjectActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    btnAddMemberDialog.setEnabled(true);
                    tilEmail.setError("Lỗi kết nối!");
                    Toast.makeText(AddProjectActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void create() {
        APIService apiService = RetrofitCilent.getRetrofit().create(APIService.class);
        Call<Map<String, Object>> stringCall = apiService.createProject(createProjectRequest);

        stringCall.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseBody = response.body();
                    String message = responseBody.get("message").toString();
                    int status = ((Number) responseBody.get("status")).intValue();
                    if (status == 201) {
                        Intent intent = new Intent(AddProjectActivity.this, HomePageActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(AddProjectActivity.this, "✅ : " + message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddProjectActivity.this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                    }
                } else if (response.errorBody() != null) {
                    try {
                        String errorJson = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(errorJson);
                        String message = jsonObject.has("message") ? jsonObject.getString("message") : "Tạo project thất bại!";
                        Toast.makeText(AddProjectActivity.this, "❌ : " + message, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(AddProjectActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddProjectActivity.this, "Lỗi: Tạo project thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(AddProjectActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void navigation() {
        ImageButton btnHome = findViewById(R.id.btnHome);
        ImageButton btnTask = findViewById(R.id.btnTask);
        ImageButton btnProfile = findViewById(R.id.btnProfile);
        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(this, HomePageActivity.class));
            overridePendingTransition(0, 0);
        });

        btnTask.setOnClickListener(v -> {
            startActivity(new Intent(this, TaskPageActivity.class));
            overridePendingTransition(0, 0);
        });

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            overridePendingTransition(0, 0);
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public void onUpdateMember(Member member, String newRole) {

    }

    @Override
    public void onDeleteMember(Member member) {

    }

    @Override
    public void onTaskUploadFailure(String errorMessage) {

    }
}