package com.example.ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui.Project;
import com.example.ui.ProjectAdapter;
import com.example.ui.R;
import com.example.ui.Task;
import com.example.ui.TaskAdapter;
import com.example.ui.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProjectDetailsActivity extends AppCompatActivity {

    private RecyclerView taskListContainer;
    private List<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);

        navigation();

        taskList = new ArrayList<>();
        taskList.add(new Task(LocalDate.now(),new User("xuandungsđ"),"TO_DO","Website Redesign"));
        taskList.add(new Task(LocalDate.now(),new User("xuandung1"),"TO_DO","Website Redesign"));
        taskList.add(new Task(LocalDate.now(),new User("xuandung2"),"TO_DO","Website Redesign"));
        taskList.add(new Task(LocalDate.now(),new User("xuandung3"),"TO_DO","Website Redesign"));
        taskList.add(new Task(LocalDate.now(),new User("xuandung4"),"TO_DO","Website Redesign"));
        taskList.add(new Task(LocalDate.now(),new User("xuandung5"),"TO_DO","Website Redesign"));
        taskList.add(new Task(LocalDate.now(),new User("xuandung6"),"TO_DO","Website Redesign"));
        taskList.add(new Task(LocalDate.now(),new User("xuandung7"),"TO_DO","Website Redesign"));taskList.add(new Task(LocalDate.now(),new User("xuandungsđ"),"TO_DO","Website Redesign"));
        taskList.add(new Task(LocalDate.now(),new User("xuandung1"),"TO_DO","Website Redesign"));
        taskList.add(new Task(LocalDate.now(),new User("xuandung2"),"TO_DO","Website Redesign"));
        taskList.add(new Task(LocalDate.now(),new User("xuandung3"),"TO_DO","Website Redesign"));
        taskList.add(new Task(LocalDate.now(),new User("xuandung4"),"TO_DO","Website Redesign"));
        taskList.add(new Task(LocalDate.now(),new User("xuandung5"),"TO_DO","Website Redesign"));
        taskList.add(new Task(LocalDate.now(),new User("xuandung6"),"TO_DO","Website Redesign"));
        taskList.add(new Task(LocalDate.now(),new User("xuandung7"),"TO_DO","Website Redesign"));



        RecyclerView recyclerView = findViewById(R.id.recyclerTasks);
        TaskAdapter adapter = new TaskAdapter(taskList); // taskList lấy từ DB hoặc API
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


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