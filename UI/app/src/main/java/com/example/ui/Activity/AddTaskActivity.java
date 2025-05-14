package com.example.ui.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui.Adapter.MemberAssignAdapter;
import com.example.ui.Enum.TaskPriority;
import com.example.ui.Model.Member;
import com.example.ui.R;
import com.example.ui.Request.TaskRequest;
import com.example.ui.Retrofit.APIService;
import com.example.ui.Retrofit.RetrofitCilent;
import com.example.ui.Upload.UploadTask;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTaskActivity extends AppCompatActivity implements UploadTask.OnTaskUploadSuccessListener {
    private static final int PICK_FILE_REQUEST = 1;
    private TextInputEditText edtTaskTitle, edtTaskDescription, edtTaskDue, edtFileName;
    private Spinner spinnerTaskPriority;
    private Button btnAddFile, btnAddProject, btnAssign, btnClearAssign;
    private Uri fileUri;
    private String fileUrl;
    private APIService apiService;
    private TextView tvAssignedTo;
    private String assignedEmail;
    private List<Member> memberList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_task);

        // Initialize views
        edtTaskTitle = findViewById(R.id.edtTaskTitle);
        edtTaskDescription = findViewById(R.id.edtTaskDecript);
        edtTaskDue = findViewById(R.id.edtTaskDue);
        edtFileName = findViewById(R.id.edtFileName);
        spinnerTaskPriority = findViewById(R.id.spinnerTaskPriority);
        btnAddFile = findViewById(R.id.btnAddFile);
        btnAddProject = findViewById(R.id.buttonAddProject);
        btnAssign = findViewById(R.id.btnAssign);
        tvAssignedTo = findViewById(R.id.tvAssignedTo);
        btnClearAssign = findViewById(R.id.btnClearAssign);

        // Initialize Retrofit
        apiService = RetrofitCilent.getRetrofit().create(APIService.class);

        // Load spinner and date picker
        loadSpinner();
        pickDateEvent();

        // Setup file upload
        btnAddFile.setOnClickListener(v -> openFilePicker());

        // Setup add task button
        btnAddProject.setOnClickListener(v -> {
            if (fileUri != null) {
                if (!isFileSupported(fileUri)) {
                    Toast.makeText(this, "Định dạng file không được hỗ trợ", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Upload file
                new UploadTask(this, fileUri, this).execute();
            } else {
                // Không có file, gửi task trực tiếp
                submitTask();
            }
        });
        btnAssign.setOnClickListener(v -> showAssignMemberDialog());

        btnClearAssign.setOnClickListener(v -> {
            assignedEmail = null;
            tvAssignedTo.setText("Assigned to: None");
            btnClearAssign.setVisibility(View.GONE);
        });
    }

    private boolean isFileSupported(Uri uri) {
        String[] supportedExtensions = {"pdf", "doc", "docx", "txt", "ppt"};
        String fileName = getFileName(uri);
        if (fileName == null) return false;
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        for (String ext : supportedExtensions) {
            if (ext.equals(extension)) {
                return true;
            }
        }
        return false;
    }

    private String getFileName(Uri uri) {
        String fileName = null;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (nameIndex != -1) {
                fileName = cursor.getString(nameIndex);
            }
            cursor.close();
        }
        if (fileName == null) {
            String path = uri.getPath();
            if (path != null) {
                fileName = path.substring(path.lastIndexOf('/') + 1);
            }
        }
        return fileName != null ? fileName : "Unknown";
    }

    private void showAssignMemberDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_assign_member, null);
        dialog.setContentView(dialogView);
        RecyclerView recyclerMembers = dialogView.findViewById(R.id.recyclerMembers);
        ProgressBar progressBar = dialogView.findViewById(R.id.progressBar);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        recyclerMembers.setLayoutManager(new LinearLayoutManager(this));
        MemberAssignAdapter adapter = new MemberAssignAdapter(this, memberList, email -> {
            assignedEmail = email;
            tvAssignedTo.setText("Assigned to: " + email);
            btnClearAssign.setVisibility(View.VISIBLE);
            dialog.dismiss();
        });
        recyclerMembers.setAdapter(adapter);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        progressBar.setVisibility(View.VISIBLE);
        APIService apiService = RetrofitCilent.getRetrofit().create(APIService.class);
        Call<Map<String, Object>> call = apiService.getAllMember(getIntent().getIntExtra("projectId", -1));
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<Map<String, Object>> data = (List<Map<String, Object>>) response.body().get("members");
                    memberList.clear();
                    for (Map<String, Object> item : data) {
                        Integer id = ((Number) item.get("id")).intValue();
                        String email = (String) item.get("email");
                        String role = (String) item.get("role");
                        String avatar = (String) item.get("avatar");
                        memberList.add(new Member(id, email, role, avatar));
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(AddTaskActivity.this, "Không thể tải danh sách thành viên", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AddTaskActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void loadSpinner() {
        String[] priorities = Arrays.stream(TaskPriority.values())
                .map(Enum::name)
                .toArray(String[]::new);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, priorities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTaskPriority.setAdapter(adapter);

        spinnerTaskPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Lưu priority được chọn
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không chọn gì
            }
        });
    }

    private void pickDateEvent() {
        edtTaskDue.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddTaskActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        edtTaskDue.setText(date);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();
            String fileName = getFileName(fileUri);
            edtFileName.setText(fileName);
        }
    }

    @Override
    public void onTaskUploadSuccess(String fileUrl) {
        this.fileUrl = fileUrl;
        submitTask();
    }

    @Override
    public void onTaskUploadFailure(String errorMessage) {

    }

    private void submitTask() {
        String title = edtTaskTitle.getText().toString().trim();
        String description = edtTaskDescription.getText().toString().trim();
        String priority = spinnerTaskPriority.getSelectedItem().toString();
        String dueDateStr = edtTaskDue.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || dueDateStr.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse chuỗi ngày từ EditText sang LocalDate
        LocalDate dueDateLocal = LocalDate.parse(dueDateStr, DateTimeFormatter.ofPattern("d/M/yyyy"));

        // Định dạng LocalDate thành chuỗi theo chuẩn ISO 8601 (yyyy-MM-dd)
        DateTimeFormatter backendDateFormatter = DateTimeFormatter.ISO_DATE;
        String dueDateForBackend = dueDateLocal.format(backendDateFormatter);

        Integer projectId = getIntent().getIntExtra("projectId", -1);

        TaskRequest taskRequest = new TaskRequest(
                title,
                description,
                priority,
                projectId,
                assignedEmail, // Sử dụng assignedEmail
                fileUrl,
                dueDateForBackend
        );

        apiService.addTask(taskRequest).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddTaskActivity.this, "Task created successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AddTaskActivity.this, "Failed to create task", Toast.LENGTH_SHORT).show();
                    // In ra lỗi response để debug (chỉ trong môi trường phát triển)
                    // Log.e("AddTaskError", "Response code: " + response.code() + ", body: " + response.errorBody().string());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(AddTaskActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                // In ra lỗi chi tiết (chỉ trong môi trường phát triển)
                // Log.e("AddTaskFailure", "Error: " + t.toString());
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}