package com.example.ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ui.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private EditText edtFullName, edtEmail;
    private Button btnEdit, btnSave;

    private ImageButton uploadImg,btnHome,btnTask, btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        anhXa();
        // Sự kiện nút Edit
        btnEdit.setOnClickListener(v -> {
            // Cho phép chỉnh sửa
            edtFullName.setEnabled(true);
            edtEmail.setEnabled(true);
            uploadImg.setVisibility(View.VISIBLE);

            // Hiện nút Save, ẩn nút Edit
            btnSave.setVisibility(View.VISIBLE);
            btnEdit.setVisibility(View.GONE);
        });

        // Sự kiện nút Save
        btnSave.setOnClickListener(v -> {
            // Tắt chế độ chỉnh sửa
            edtFullName.setEnabled(false);
            edtEmail.setEnabled(false);

            // Ẩn nút Save, hiện lại nút Edit
            uploadImg.setVisibility(View.GONE);
            btnSave.setVisibility(View.GONE);
            btnEdit.setVisibility(View.VISIBLE);

        });

        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(this, HomePageActivity.class));
            overridePendingTransition(0,0);
        });

        btnTask.setOnClickListener(v -> {
            startActivity(new Intent(this, TaskPageActivity.class));
            overridePendingTransition(0,0);
        });

        btnProfile.setOnClickListener(v -> {
            //
        });
    }

    void anhXa(){
        btnHome = findViewById(R.id.btnHome);
        btnTask = findViewById(R.id.btnTask);
        btnProfile = findViewById(R.id.btnProfile);
        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        btnEdit = findViewById(R.id.btnEdit);
        btnSave = findViewById(R.id.btnSave);
        uploadImg = findViewById(R.id.btnUploadImg);

    }
}