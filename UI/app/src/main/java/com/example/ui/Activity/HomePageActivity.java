package com.example.ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ui.Model.Project;
import com.example.ui.Adapter.ProjectAdapter;
import com.example.ui.R;

import java.util.ArrayList;
import java.util.List;

public class HomePageActivity extends AppCompatActivity {

    private LinearLayout projectListContainer;
    private List<Project> projectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        projectListContainer = findViewById(R.id.projectListContainer);

        // Dữ liệu test
        projectList = new ArrayList<>();
        projectList.add(new Project("Website Redesign", 3));
        projectList.add(new Project("Marketing Campaign", 5));
        projectList.add(new Project("Mobile App", 4));
        projectList.add(new Project("Website Redesign", 3));
        projectList.add(new Project("Marketing Campaign", 5));
        projectList.add(new Project("Mobile App", 4));
        projectList.add(new Project("Website Redesign", 3));
//        projectList.add(new Project("Marketing Campaign", 5));
//        projectList.add(new Project("Mobile App", 4));
//        projectList.add(new Project("Website Redesign", 3));
//        projectList.add(new Project("Marketing Campaign", 5));
//        projectList.add(new Project("Mobile App", 4));
//        projectList.add(new Project("Website Redesign", 3));
//        projectList.add(new Project("Marketing Campaign", 5));
//        projectList.add(new Project("Mobile App", 4));
//        projectList.add(new Project("Website Redesign", 3));
//        projectList.add(new Project("Marketing Campaign", 5));
//        projectList.add(new Project("Mobile App", 4));

        // Load lên
        ProjectAdapter adapter = new ProjectAdapter(this, projectList, projectListContainer);
        adapter.loadProjects();

    }
    void navigation(){
        ImageButton btnHome = findViewById(R.id.btnHome);
        ImageButton btnTask = findViewById(R.id.btnTask);
        ImageButton btnProfile = findViewById(R.id.btnProfile);
        btnHome.setOnClickListener(v -> {
            // Xử lý chuyển trang về Home
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
