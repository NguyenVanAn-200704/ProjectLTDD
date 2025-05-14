package com.example.ui.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui.Adapter.TaskTempAdapter;
import com.example.ui.Model.TaskTemp;
import com.example.ui.R;
import com.example.ui.Retrofit.APIService;
import com.example.ui.Retrofit.RetrofitCilent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskPageActivity extends AppCompatActivity implements TaskTempAdapter.OnTaskClickListener {

    private RecyclerView recyclerView;
    private TaskTempAdapter adapter;
    private List<TaskTemp> allTasks = new ArrayList<>();
    private List<TaskTemp> filteredTasks = new ArrayList<>();
    private EditText edtSearchTask;
    private ImageButton btnSearch, btnFilter;
    private String currentSearchQuery = "";
    private String selectedStatus = "";
    private String selectedPriority = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_page);

        recyclerView = findViewById(R.id.taskListContainer);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskTempAdapter(this, filteredTasks, this); // Sử dụng filteredTasks cho adapter
        recyclerView.setAdapter(adapter);

        edtSearchTask = findViewById(R.id.edtSearchTask);
        btnSearch = findViewById(R.id.btnSearch);
        btnFilter = findViewById(R.id.btnFilter);

        int userId = getSharedPreferences("UserPreferences", MODE_PRIVATE).getInt("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy userId. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            fetchTasksForUser(userId);
        }

        setupSearch();
        setupFilter();
        navigation();
    }

    private void fetchTasksForUser(Integer userId) {
        APIService apiService = RetrofitCilent.getRetrofit().create(APIService.class);
        Call<Map<String, Object>> call = apiService.allTasksInUser(userId);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(TaskPageActivity.this, "Không thể lấy danh sách nhiệm vụ.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> responseBody = response.body();
                Object tasksObj = responseBody.get("tasks");

                if (!(tasksObj instanceof List)) {
                    Toast.makeText(TaskPageActivity.this, "Dữ liệu không hợp lệ.", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Map<String, Object>> data = (List<Map<String, Object>>) tasksObj;
                allTasks.clear(); // Clear danh sách cũ
                filteredTasks.clear();

                for (Map<String, Object> taskMap : data) {
                    Number idNumber = (Number) taskMap.get("id");
                    Integer id = idNumber != null ? idNumber.intValue() : null;
                    String title = (String) taskMap.get("title");
                    String dueDateStr = (String) taskMap.get("dueDate");
                    String status = (String) taskMap.get("status"); // Cần status cho filter
                    String priority = (String) taskMap.get("priority"); // Cần priority cho filter

                    if (id == null || title == null || dueDateStr == null) continue;

                    try {
                        LocalDate dueDate = LocalDate.parse(dueDateStr);
                        allTasks.add(new TaskTemp(id, title, dueDate, status, priority)); // Giả sử TaskTemp có thêm status và priority
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                applyFilters(); // Gọi applyFilters sau khi tải dữ liệu
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(TaskPageActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

        // Thiết lập spinner trạng thái
        List<String> statusList = Arrays.asList("", "TO_DO", "IN_PROGRESS", "DONE");
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusList);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);
        spinnerStatus.setSelection(statusList.indexOf(selectedStatus));

        // Thiết lập spinner ưu tiên
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
        filteredTasks.clear();
        List<TaskTemp> tempList = allTasks;

        // Lọc theo tìm kiếm
        if (!currentSearchQuery.isEmpty()) {
            String query = currentSearchQuery.toLowerCase();
            tempList = tempList.stream()
                    .filter(task -> task.getTitle().toLowerCase().contains(query))
                    .collect(Collectors.toList());
        }

        // Lọc theo trạng thái
        if (!selectedStatus.isEmpty()) {
            tempList = tempList.stream()
                    .filter(task -> task.getStatus() != null && task.getStatus().equals(selectedStatus))
                    .collect(Collectors.toList());
        }

        // Lọc theo ưu tiên
        if (!selectedPriority.isEmpty()) {
            tempList = tempList.stream()
                    .filter(task -> task.getPriority() != null && task.getPriority().equals(selectedPriority))
                    .collect(Collectors.toList());
        }

        filteredTasks.addAll(tempList);
        adapter.notifyDataSetChanged();
    }

    private void navigation() {
        ImageButton btnHome = findViewById(R.id.btnHome);
        ImageButton btnTask = findViewById(R.id.btnTask);
        ImageButton btnProfile = findViewById(R.id.btnProfile);

        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(this, HomePageActivity.class));
            finish();
        });

        btnTask.setOnClickListener(v -> {
            // Đã ở TaskPageActivity
        });

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            overridePendingTransition(0, 0);
        });
    }

    @Override
    public void onTaskClick(int taskId) {
        Intent intent = new Intent(this, TaskDetailActivity.class);
        intent.putExtra("taskId", taskId);
        startActivity(intent);
    }
}