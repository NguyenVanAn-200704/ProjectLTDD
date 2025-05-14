package com.example.ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ui.Model.Project;
import com.example.ui.R;
import com.example.ui.Adapter.ProjectAdapter;
import com.example.ui.Retrofit.APIService;
import com.example.ui.Retrofit.RetrofitCilent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePageActivity extends AppCompatActivity {

    private LinearLayout projectListContainer;
    private List<Project> projectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        projectListContainer = findViewById(R.id.projectListContainer);
        projectList = new ArrayList<>();

        int userId = getSharedPreferences("UserPreferences", MODE_PRIVATE).getInt("userId", -1);
        if (userId == -1) {
            Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Không tìm thấy userId. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
        } else {
            fetchProjectsFromAPI(userId);
        }
        navigation();
    }

    private void fetchProjectsFromAPI(Integer userId) {
        Log.d("HomePage", "Fetching projects for userId: " + userId);
        APIService apiService = RetrofitCilent.getRetrofit().create(APIService.class);
        Call<Map<String, Object>> call = apiService.allProjects(userId);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Map<String, Object>> data = (List<Map<String, Object>>) response.body().get("data");
                    Log.d("HomePage", "Projects count: " + data.size());

                    projectList.clear();
                    for (Map<String, Object> item : data) {
                        Integer id = ((Double) item.get("id")).intValue();
                        String name = (String) item.get("name");
                        Integer createBy = ((Number) item.get("createBy")).intValue();
                        int memberCount = ((Double) item.get("memberCount")).intValue();
                        projectList.add(new Project(id, createBy, name, memberCount));
                    }

                    if (projectList.isEmpty()) {
                        Toast.makeText(HomePageActivity.this, "Không có dự án nào", Toast.LENGTH_SHORT).show();
                    }

                    ProjectAdapter adapter = new ProjectAdapter(HomePageActivity.this, projectList, projectListContainer, userId) {
                        @Override
                        public void onProjectClick(Project project) {
                            // Không cần override vì logic được xử lý trong ProjectAdapter
                        }
                    };
                    adapter.loadProjects();
                } else {
                    Log.e("HomePage", "Projects API failed: " + response.code());
                    Toast.makeText(HomePageActivity.this, "Lỗi lấy danh sách dự án: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e("HomePage", "Projects API error: " + t.getMessage());
                Toast.makeText(HomePageActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    void navigation() {
        ImageButton btnHome = findViewById(R.id.btnHome);
        ImageButton btnTask = findViewById(R.id.btnTask);
        ImageButton btnProfile = findViewById(R.id.btnProfile);
        Button btnNewProject = findViewById(R.id.btnNewProject);
        btnHome.setOnClickListener(v -> {
        });

        btnTask.setOnClickListener(v -> {
            startActivity(new Intent(this, TaskPageActivity.class));
            overridePendingTransition(0, 0);
        });

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            overridePendingTransition(0, 0);
        });

        btnNewProject.setOnClickListener(v->{
            startActivity(new Intent(this, AddProjectActivity.class));
            finish();
        });
    }
}
