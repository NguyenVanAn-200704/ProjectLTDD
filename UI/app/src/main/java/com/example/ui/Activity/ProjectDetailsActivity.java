package com.example.ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui.R;
import com.example.ui.Model.Task;
import com.example.ui.Adapter.TaskAdapter;
import com.example.ui.Model.User;
import com.example.ui.Retrofit.APIService;
import com.example.ui.Retrofit.RetrofitCilent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class ProjectDetailsActivity extends AppCompatActivity {

    private RecyclerView taskListContainer;
    private List<Task> taskList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);

        int projectId = getSharedPreferences("UserPreferences", MODE_PRIVATE).getInt("projectId", -1);
        if (projectId == -1) {
            Intent intent = new Intent(ProjectDetailsActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Không tìm thấy userId. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
        } else {
            fetchTasksFromAPI(projectId);
        }
        RecyclerView recyclerView = findViewById(R.id.recyclerTasks);
        TaskAdapter adapter = new TaskAdapter(taskList); // taskList lấy từ DB hoặc API
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

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
                Toast.makeText(ProjectDetailsActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void navigation(){
        ImageButton btnHome = findViewById(R.id.btnHome);
        ImageButton btnTask = findViewById(R.id.btnTask);
        ImageButton btnProfile = findViewById(R.id.btnProfile);
        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(this, HomePageActivity.class));
            finish();
        });

        btnTask.setOnClickListener(v -> {
            startActivity(new Intent(this, TaskPageActivity.class));
            overridePendingTransition(0,0);
        });

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            overridePendingTransition(0,0);
        });

    }
}