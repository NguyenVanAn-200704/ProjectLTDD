package com.example.ui.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.example.ui.Enum.ProjectRole;
import com.example.ui.Model.Member;
import com.example.ui.Model.Task;
import com.example.ui.Model.User;
import com.example.ui.R;
import com.example.ui.Request.UpdateProjectMemberRequest;
import com.example.ui.Retrofit.APIService;
import com.example.ui.Retrofit.RetrofitCilent;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectDetailsActivity extends AppCompatActivity implements MemberAdapter.OnMemberActionListener {

    private RecyclerView taskListContainer;
    private List<Task> taskList = new ArrayList<>();
    private List<Task> filteredTaskList = new ArrayList<>();
    private List<Member> memberList = new ArrayList<>();
    private MemberAdapter memberAdapter;
    private TaskAdapter taskAdapter;
    private Button btnAddMember, btnNewTask;
    private ImageButton btnSearch, btnFilter;
    private TextInputEditText edtSearchTask;
    private int projectId;
    private int userId;
    private int projectCreatorId;
    private String currentSearchQuery = "";
    private String selectedStatus = "";
    private String selectedPriority = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);

        // Initialize views
        taskListContainer = findViewById(R.id.recyclerTasks);
        btnAddMember = findViewById(R.id.btnAddMemberDialog);
        btnNewTask = findViewById(R.id.btnNewTask);
        btnSearch = findViewById(R.id.btnSearch);
        btnFilter = findViewById(R.id.btnFilter);
        edtSearchTask = findViewById(R.id.edtSearchTask);

        // Get data from Intent
        projectId = getIntent().getIntExtra("projectId", -1);
        projectCreatorId = getIntent().getIntExtra("projectCreatorId", -1);
        String projectName = getIntent().getStringExtra("projectName");
        String role = getIntent().getStringExtra("role");
        ProjectRole projectRole = role != null ? ProjectRole.valueOf(role) : ProjectRole.VIEWER;

        // Get userId from SharedPreferences
        userId = getSharedPreferences("UserPreferences", MODE_PRIVATE).getInt("userId", -1);
        if (userId == -1) {
            Intent intent = new Intent(ProjectDetailsActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Không tìm thấy userId. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set project title
        TextView tvProjectTitle = findViewById(R.id.tvProjectTitle);
        tvProjectTitle.setText(projectName);

        // Control UI based on role
        if (projectRole == ProjectRole.ADMIN) {
            btnAddMember.setVisibility(View.VISIBLE);
            btnNewTask.setVisibility(View.VISIBLE);
        } else {
            btnAddMember.setVisibility(View.GONE);
            btnNewTask.setVisibility(View.GONE);
        }

        // Initialize MemberAdapter
        memberAdapter = new MemberAdapter(this, memberList, userId, projectCreatorId, this);
        fetchTasksFromAPI(projectId);
        // Initialize TaskASortByNameaTaskAdapter
        filteredTaskList.addAll(taskList);
        taskAdapter = new TaskAdapter(filteredTaskList, role, userId);
        taskListContainer.setLayoutManager(new LinearLayoutManager(this));
        taskListContainer.setAdapter(taskAdapter);
        taskAdapter.notifyDataSetChanged();

        // Set up button listeners
        btnAddMember.setOnClickListener(v -> showAddMemberDialog());
        btnNewTask.setOnClickListener(v -> {
            Intent intent = new Intent(ProjectDetailsActivity.this, AddTaskActivity.class);
            intent.putExtra("projectId", projectId);
            intent.putExtra("userId", userId);
            startActivityForResult(intent, 123);
        });


        // Set up search and filter
        setupSearch();
        setupFilter();

        navigation();
    }

    private void fetchTasksFromAPI(Integer projectId) {
        APIService apiService = RetrofitCilent.getRetrofit().create(APIService.class);
        Call<Map<String, Object>> call = apiService.allTasksInProject(projectId);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseBody = response.body();
                    if (responseBody.containsKey("tasks")) {
                        List<Map<String, Object>> data = (List<Map<String, Object>>) responseBody.get("tasks");

                        taskList.clear();
                        filteredTaskList.clear();
                        for (Map<String, Object> taskMap : data) {
                            Number idNumber = (Number) taskMap.get("id");
                            int id = idNumber.intValue();
                            String title = (String) taskMap.get("title");
                            String status = (String) taskMap.get("status");
                            String priority = (String) taskMap.get("priority");
                            String dueDateStr = (String) taskMap.get("dueDate");

                            User assignedUser = null;
                            Map<String, Object> userMap = (Map<String, Object>) taskMap.get("user");
                            if (userMap != null) {
                                Integer userId = userMap.get("id") != null ? ((Number) userMap.get("id")).intValue() : null;
                                String userName = (String) userMap.get("name");
                                String userEmail = (String) userMap.get("email");
                                if (userId != null && userName != null && userEmail != null) {
                                    assignedUser = new User(userName, userEmail, userId);
                                }
                            }

                            LocalDate dueDate = dueDateStr != null ? LocalDate.parse(dueDateStr) : null;
                            Task task = new Task(id, dueDate, assignedUser, status, title, priority);
                            taskList.add(task);
                        }

                        applyFilters();
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

    private void setupSearch() {
        edtSearchTask.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                currentSearchQuery = s.toString().trim();
                applyFilters();
            }
        });

        btnSearch.setOnClickListener(v -> applyFilters());
    }

    private void setupFilter() {
        btnFilter.setOnClickListener(v -> showFilterDialog());
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_filter_tasks, null);
        builder.setView(dialogView);

        Spinner spinnerStatus = dialogView.findViewById(R.id.spinnerStatus);
        Spinner spinnerPriority = dialogView.findViewById(R.id.spinnerPriority);
        Button btnApplyFilter = dialogView.findViewById(R.id.btnApplyFilter);

        List<String> statusList = Arrays.asList("", "TO_DO", "IN_PROGRESS", "DONE");
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusList);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);
        spinnerStatus.setSelection(statusList.indexOf(selectedStatus));

        List<String> priorityList = Arrays.asList("", "LOW", "MEDIUM", "HIGH");
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, priorityList);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);
        spinnerPriority.setSelection(priorityList.indexOf(selectedPriority));

        AlertDialog dialog = builder.create();

        btnApplyFilter.setOnClickListener(v -> {
            selectedStatus = spinnerStatus.getSelectedItem().toString();
            selectedPriority = spinnerPriority.getSelectedItem().toString();
            applyFilters();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void applyFilters() {
        filteredTaskList.clear();
        List<Task> tempList = taskList;

        if (!currentSearchQuery.isEmpty()) {
            String query = currentSearchQuery.toLowerCase();
            tempList = tempList.stream()
                    .filter(task -> task.getTitle().toLowerCase().contains(query))
                    .collect(Collectors.toList());
        }

        if (!selectedStatus.isEmpty()) {
            tempList = tempList.stream()
                    .filter(task -> task.getStatus().equals(selectedStatus))
                    .collect(Collectors.toList());
        }

        if (!selectedPriority.isEmpty()) {
            tempList = tempList.stream()
                    .filter(task -> task.getPriority() != null && task.getPriority().equals(selectedPriority))
                    .collect(Collectors.toList());
        }

        filteredTaskList.addAll(tempList);
        taskAdapter.notifyDataSetChanged();
    }

    private void showAddMemberDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_member, null);
        dialog.setContentView(dialogView);
        View parentLayout = dialogView.getParent() instanceof View ? (View) dialogView.getParent() : null;
        if (parentLayout != null) {
            parentLayout.getLayoutParams().height = (int) (getResources().getDisplayMetrics().heightPixels * 0.9);
        }

        TextInputEditText edtEmail = dialogView.findViewById(R.id.edtEmail);
        TextInputLayout tilEmail = dialogView.findViewById(R.id.tilEmail);
        Spinner spinnerRole = dialogView.findViewById(R.id.spinnerRole);
        Button btnAddMemberDialog = dialogView.findViewById(R.id.btnAddMember);
        RecyclerView recyclerMembers = dialogView.findViewById(R.id.recyclerMembers);
        ProgressBar progressBar = dialogView.findViewById(R.id.progressBar);

        List<String> roles = Arrays.asList("ADMIN", "MEMBER", "VIEWER");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(spinnerAdapter);
        spinnerRole.setSelection(1);

        recyclerMembers.setLayoutManager(new LinearLayoutManager(this));
        recyclerMembers.setAdapter(memberAdapter);

        loadMember();

        btnAddMemberDialog.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String role = spinnerRole.getSelectedItem() != null ? spinnerRole.getSelectedItem().toString() : "MEMBER";

            if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
                tilEmail.setError("Vui lòng nhập email hợp lệ!");
                return;
            }

            tilEmail.setError(null);
            progressBar.setVisibility(View.VISIBLE);
            btnAddMemberDialog.setEnabled(false);

            APIService apiService = RetrofitCilent.getRetrofit().create(APIService.class);
            Call<Map<String, Object>> checkCall = apiService.checkUserByEmail(email);
            checkCall.enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Map<String, Object> responseBody = response.body();
                        int status = ((Number) responseBody.get("status")).intValue();

                        if (status == 200) {
                            Call<Map<String, Object>> addCall = apiService.addMember(new Member(projectId, email, role));
                            addCall.enqueue(new Callback<Map<String, Object>>() {
                                @Override
                                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                                    progressBar.setVisibility(View.GONE);
                                    btnAddMemberDialog.setEnabled(true);

                                    if (response.isSuccessful() && response.body() != null) {
                                        Toast.makeText(ProjectDetailsActivity.this, "Thêm thành viên thành công!", Toast.LENGTH_SHORT).show();
                                        edtEmail.setText("");
                                        loadMember();
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

    private void loadMember() {
        APIService apiService = RetrofitCilent.getRetrofit().create(APIService.class);
        Call<Map<String, Object>> memberCall = apiService.getAllMember(projectId);

        memberCall.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Map<String, Object>> data = (List<Map<String, Object>>) response.body().get("members");
                    memberList.clear();
                    for (Map<String, Object> item : data) {
                        Integer id = ((Number) item.get("id")).intValue();
                        String email = (String) item.get("email");
                        String role = (String) item.get("role");
                        String avatar = (String) item.get("avatar");
                        Integer userId = ((Number) item.get("userId")).intValue();
                        memberList.add(new Member(id, userId, email, role, avatar));
                    }
                    memberAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(ProjectDetailsActivity.this, "Không thể tải danh sách thành viên", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onUpdateMember(Member member, String newRole) {
        APIService apiService = RetrofitCilent.getRetrofit().create(APIService.class);
        Call<Map<String, Object>> updateCall = apiService.updateMember(new UpdateProjectMemberRequest(member.getId(), newRole, userId));
        updateCall.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseBody = response.body();
                    int status = ((Number) responseBody.get("status")).intValue();
                    String message = responseBody.get("message").toString();
                    if (status == 200) {
                        Toast.makeText(ProjectDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                        loadMember();
                    } else {
                        Toast.makeText(ProjectDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProjectDetailsActivity.this, "Lỗi khi cập nhật vai trò", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(ProjectDetailsActivity.this, "Lỗi kết nối khi cập nhật: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDeleteMember(Member member) {
        APIService apiService = RetrofitCilent.getRetrofit().create(APIService.class);
        Call<Map<String, Object>> deleteCall = apiService.deleteMember(member.getId());
        deleteCall.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseBody = response.body();
                    int status = ((Number) responseBody.get("status")).intValue();
                    String message = responseBody.get("message").toString();
                    if (status == 200) {
                        Toast.makeText(ProjectDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                        loadMember();
                    } else {
                        Toast.makeText(ProjectDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProjectDetailsActivity.this, "Lỗi khi xóa thành viên", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(ProjectDetailsActivity.this, "Lỗi kết nối khi xóa: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode,data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            fetchTasksFromAPI(projectId);
        }
    }

    @Override
    public void onTaskUploadFailure(String errorMessage) {
        Toast.makeText(this, "Tải file thất bại: " + errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void navigation() {
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