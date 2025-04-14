package com.example.ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ui.R;
import com.example.ui.Model.TaskTemp;
import com.example.ui.Adapter.TaskTempAdapter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskPageActivity extends AppCompatActivity {

    private LinearLayout taslListContainer;
    private List<TaskTemp> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_page);

        taslListContainer = findViewById(R.id.taskListContainer);

        // Dữ liệu test
        taskList = new ArrayList<>();
        taskList.add(new TaskTemp("Website Redesign", LocalDate.now()));
        taskList.add(new TaskTemp("Marketing Campaign", LocalDate.now()));
        taskList.add(new TaskTemp("Mobile App", LocalDate.now()));
        taskList.add(new TaskTemp("Website Redesign", LocalDate.now()));
        taskList.add(new TaskTemp("Website Redesign", LocalDate.now()));
        taskList.add(new TaskTemp("Marketing Campaign", LocalDate.now()));
//        taskList.add(new Task("Mobile App", LocalDate.now()));
//        taskList.add(new Task("Website Redesign", LocalDate.now()));
//        taskList.add(new Task("Website Redesign", LocalDate.now()));
//        taskList.add(new Task("Marketing Campaign", LocalDate.now()));
//        taskList.add(new Task("Mobile App", LocalDate.now()));
//        taskList.add(new Task("Website Redesign", LocalDate.now()));
//        taskList.add(new Task("Website Redesign", LocalDate.now()));
//        taskList.add(new Task("Marketing Campaign", LocalDate.now()));
//        taskList.add(new Task("Mobile App", LocalDate.now()));
//        taskList.add(new Task("Website Redesign", LocalDate.now()));

        // Load lên
        TaskTempAdapter adapter = new TaskTempAdapter(this, taskList, taslListContainer);
        adapter.loadTasks();

        LinearLayout navLayout = findViewById(R.id.navigation);

        ImageButton btnHome = findViewById(R.id.btnHome);
        ImageButton btnTask = findViewById(R.id.btnTask);
        ImageButton btnProfile = findViewById(R.id.btnProfile);
        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(this, HomePageActivity.class));
            overridePendingTransition(0,0);
        });

        btnTask.setOnClickListener(v -> {
            //
        });

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            overridePendingTransition(0,0);
        });
    }
}