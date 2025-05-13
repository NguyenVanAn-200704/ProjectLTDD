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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui.Adapter.MemberAdapter;
import com.example.ui.Adapter.TaskAdapter;
import com.example.ui.Model.Member;
import com.example.ui.Model.Task;
import com.example.ui.Model.User;
import com.example.ui.R;
import com.example.ui.Retrofit.APIService;
import com.example.ui.Retrofit.RetrofitCilent;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectDetailsActivity extends AppCompatActivity {

    private RecyclerView taskListContainer;
    private List<Task> taskList = new ArrayList<>();
    private List<Member> memberList = new ArrayList<>();
    private MemberAdapter memberAdapter;
    private Button btnAddMember;

    private int projectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);

        taskListContainer = findViewById(R.id.recyclerTasks);
        btnAddMember = findViewById(R.id.btnAddMemberDialog);

        memberAdapter = new MemberAdapter(this, memberList);

        int userId = getSharedPreferences("UserPreferences", MODE_PRIVATE).getInt("userId", -1);
        projectId = getSharedPreferences("UserPreferences", MODE_PRIVATE).getInt("projectId", -1);
        if (userId == -1) {
            Intent intent = new Intent(ProjectDetailsActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Không tìm thấy userId. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
        } else {
            fetchTasksFromAPI(projectId);
        }

        String projectName = getIntent().getStringExtra("projectName");
        TextView tvProjectTitle = findViewById(R.id.tvProjectTitle);
        tvProjectTitle.setText(projectName);


        RecyclerView recyclerView = findViewById(R.id.recyclerTasks);
        TaskAdapter taskAdapter = new TaskAdapter(taskList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(taskAdapter);

        btnAddMember.setOnClickListener(v -> showAddMemberDialog());

        navigation();
    }

    void fetchTasksFromAPI(Integer projectId) {
        APIService apiService = RetrofitCilent.getRetrofit().create(APIService.class);
        Call<Map<String, Object>> call = apiService.allTasksInProject(projectId);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, retrofit2.Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseBody = response.body();
                    if (responseBody.containsKey("tasks")) {
                        List<Map<String, Object>> data = (List<Map<String, Object>>) responseBody.get("tasks");

                        taskList.clear(); // Xóa dữ liệu cũ nếu có

                        for (Map<String, Object> taskMap : data) {
                            String title = (String) taskMap.get("title");
                            String status = (String) taskMap.get("status");
                            String dueDateStr = (String) taskMap.get("dueDate");

                            Map<String, String> userMap = (Map<String, String>) taskMap.get("user");
                            String userName = userMap.get("name");

                            LocalDate dueDate = LocalDate.parse(dueDateStr);
                            Task task = new Task(dueDate, new User(userName), status, title);
                            taskList.add(task);
                        }

                        // Cập nhật RecyclerView
                        RecyclerView recyclerView = findViewById(R.id.recyclerTasks);
                        TaskAdapter adapter = new TaskAdapter(taskList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(ProjectDetailsActivity.this));
                        recyclerView.setAdapter(adapter);
                    }
                } else {
                    Toast.makeText(ProjectDetailsActivity.this, "Lỗi lấy danh sách task", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(ProjectDetailsActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddMemberDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_member, null);
        dialog.setContentView(dialogView);
        View parentLayout = dialogView.getParent() instanceof View ? (View) dialogView.getParent() : null;
        if (parentLayout != null) {
            parentLayout.getLayoutParams().height = (int) (getResources().getDisplayMetrics().heightPixels * 0.9); // Chiếm 85% chiều cao màn hình
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

        APIService apiService = RetrofitCilent.getRetrofit().create(APIService.class);
        Call<Map<String, Object>> memberCall = apiService.getAllMember(projectId);
        memberCall.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Map<String, Object>> data = (List<Map<String, Object>>) response.body().get("members");
                    memberList.clear();
                    for (Map<String, Object> item : data) {
                        String email = (String) item.get("email");
                        String role = (String) item.get("role");
                        String avatar = (String) item.get("avatar"); // nếu có
                        memberList.add(new Member(email, role, avatar));
                    }
                    memberAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(ProjectDetailsActivity.this, "Không thể tải danh sách thành viên", Toast.LENGTH_SHORT).show();
            }
        });

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

            // Bước 1: Kiểm tra email tồn tại
            Call<Map<String, Object>> checkCall = apiService.checkUserByEmail(email);
            checkCall.enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Map<String, Object> responseBody = response.body();
                        int status = ((Number) responseBody.get("status")).intValue();

                        if (status == 200) {
                            Map<String, Object> userData = (Map<String, Object>) responseBody.get("data");

                            // Bước 2: Thêm thành viên vào project
                            Call<Map<String, Object>> addCall = apiService.addMember(new Member(projectId,email,role));
                            addCall.enqueue(new Callback<Map<String, Object>>() {
                                @Override
                                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                                    progressBar.setVisibility(View.GONE);
                                    btnAddMemberDialog.setEnabled(true);

                                    if (response.isSuccessful() && response.body() != null) {
                                        Toast.makeText(ProjectDetailsActivity.this, "Thêm thành viên thành công!", Toast.LENGTH_SHORT).show();
                                        edtEmail.setText("");
                                        loadMember(); // Load lại danh sách
                                    } else {
                                        Toast.makeText(ProjectDetailsActivity.this, "Lỗi khi thêm thành viên vào project", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                                    progressBar.setVisibility(View.GONE);
                                    btnAddMemberDialog.setEnabled(true);
                                    Toast.makeText(ProjectDetailsActivity.this, "Lỗi kết nối khi thêm thành viên", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            progressBar.setVisibility(View.GONE);
                            btnAddMemberDialog.setEnabled(true);
                            String message = responseBody.get("message").toString();
                            tilEmail.setError(message);
                            Toast.makeText(ProjectDetailsActivity.this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        btnAddMemberDialog.setEnabled(true);
                        tilEmail.setError("Email không hợp lệ hoặc không tồn tại.");
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    btnAddMemberDialog.setEnabled(true);
                    tilEmail.setError("Lỗi kết nối khi kiểm tra email");
                }
            });
        });

        dialog.show();
    }

    void loadMember() {

    }

    void checkMember()
    {

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
}
