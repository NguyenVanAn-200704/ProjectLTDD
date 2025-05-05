package com.example.ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

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

public class HomePageActivity extends AppCompatActivity {
    private LinearLayout projectListContainer;
    private List<Project> projectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        projectListContainer = findViewById(R.id.projectListContainer);
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
        APIService apiService = RetrofitCilent.getRetrofit().create(APIService.class);
        Call<Map<String, Object>> call = apiService.allProjects(userId);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Project> projects = new ArrayList<>();
                    List<Map<String, Object>> data = (List<Map<String, Object>>) response.body().get("data");

                    for (Map<String, Object> item : data) {
                        Integer id = ((Double) item.get("id")).intValue(); // JSON number -> Double
                        String name = (String) item.get("name");
                        int memberCount = ((Double) item.get("memberCount")).intValue();
                        projects.add(new Project(id, name, memberCount));
                    }
                    ProjectAdapter adapter = new ProjectAdapter(HomePageActivity.this, projects, projectListContainer);
                    adapter.loadProjects();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                t.printStackTrace();
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
