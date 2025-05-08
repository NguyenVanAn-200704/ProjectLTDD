package com.example.ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui.Adapter.TaskAdapter;
import com.example.ui.Adapter.TaskTempAdapter;
import com.example.ui.Model.Task;
import com.example.ui.Model.TaskTemp;
import com.example.ui.Model.User;
import com.example.ui.R;
import com.example.ui.Retrofit.APIService;
import com.example.ui.Retrofit.RetrofitCilent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskPageActivity extends AppCompatActivity {

    private List<Task> taskList = new ArrayList<>();
    private LinearLayout linearLayout;
    private TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_page);

        linearLayout = findViewById(R.id.taskListContainer);
        adapter = new TaskAdapter(taskList);

        int userId = getSharedPreferences("UserPreferences", MODE_PRIVATE).getInt("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy userId. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            fetchTasksForUser(userId);
        }

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
                List<TaskTemp> taskTemps = new ArrayList<>();

                for (Map<String, Object> taskMap : data) {
                    String title = (String) taskMap.get("title");
                    String dueDateStr = (String) taskMap.get("dueDate");

                    // Nếu thiếu trường nào thì bỏ qua task đó
                    if (title == null || dueDateStr == null) continue;

                    try {
                        LocalDate dueDate = LocalDate.parse(dueDateStr);
                        taskTemps.add(new TaskTemp(title, dueDate));
                    } catch (Exception e) {
                        e.printStackTrace(); // log lỗi nếu định dạng ngày sai
                    }
                }

                // Hiển thị danh sách lên giao diện
                LinearLayout taskContainer = findViewById(R.id.taskListContainer); // ID trong activity_task_page.xml
                TaskTempAdapter adapter = new TaskTempAdapter(TaskPageActivity.this, taskTemps, taskContainer);
                adapter.loadTasks();
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(TaskPageActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
}
