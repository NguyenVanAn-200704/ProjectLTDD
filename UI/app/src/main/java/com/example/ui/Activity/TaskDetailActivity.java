package com.example.ui.Activity;

import static com.example.ui.Enum.ProjectRole.ADMIN;
import static com.example.ui.Enum.ProjectRole.MEMBER;

import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui.Adapter.MemberAssignAdapter;
import com.example.ui.Enum.ProjectRole;
import com.example.ui.Enum.TaskPriority;
import com.example.ui.Enum.TaskStatus;
import com.example.ui.Model.Member;
import com.example.ui.Model.Task;
import com.example.ui.Model.User;
import com.example.ui.R;
import com.example.ui.Request.UpdateTaskRequest;
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

public class TaskDetailActivity extends AppCompatActivity implements UploadTask.OnTaskUploadSuccessListener {
    private static final int PICK_FILE_REQUEST = 2;
    private TextInputEditText edtTaskTitle, edtTaskDescription, edtTaskDue;
    private TextView tvCreatedOn, tvAssignedTo, tvFileUrl;
    private Spinner spinnerTaskPriority, spinnerTaskStatus;
    private Button btnEdit, btnAddFile, btnAssign, btnClearAssign;
    private ImageView imgTaskIcon;

    private int userId;
    private String role;
    private Uri fileUri;
    private String fileUrl;
    private APIService apiService;
    private int taskId;
    private int projectId;
    private String assignedEmail;
    private List<Member> memberList = new ArrayList<>();
    private Task currentTask;
    private ProgressDialog progressDialog;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter isoDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_detail);

        anhxa();
        apiService = RetrofitCilent.getRetrofit().create(APIService.class);
        taskId = getIntent().getIntExtra("taskId", -1);
        role = getIntent().getStringExtra("role");
        userId = getIntent().getIntExtra("userId", -1);

        if (taskId == -1 || userId == -1) {
            Toast.makeText(this, "Không tìm thấy ID task hoặc user!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadPrioritySpinner();
        loadStatusSpinner();
        pickDateEvent();
        setupButtonListeners();
        fetchTaskDetails(taskId);
        navigation();
    }

    // Thêm phương thức applyRolePermissions
    private void applyRolePermissions() {
        ProjectRole projectRole;
        try {
            projectRole = role != null ? ProjectRole.valueOf(role) : ProjectRole.VIEWER;
        } catch (IllegalArgumentException e) {
            projectRole = ProjectRole.VIEWER;
        }

        switch (projectRole) {
            case VIEWER:
                // Ẩn tất cả button
                btnAssign.setVisibility(View.GONE);
                btnClearAssign.setVisibility(View.GONE);
                btnAddFile.setVisibility(View.GONE);
                btnEdit.setVisibility(View.GONE);
                // Vô hiệu hóa tất cả input
                edtTaskTitle.setEnabled(false);
                edtTaskDescription.setEnabled(false);
                edtTaskDue.setEnabled(false);
                spinnerTaskPriority.setEnabled(false);
                spinnerTaskStatus.setEnabled(false);
                break;

            case MEMBER:
                // Kiểm tra xem user hiện tại có phải là assigned user không
                Call<Map<String, Object>> userCall = apiService.profile(userId);
                userCall.enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Map<String, Object> userData = (Map<String, Object>) response.body().get("user");
                            String userEmail = (String) userData.get("email");
                            boolean isAssignedUser = assignedEmail != null && assignedEmail.equals(userEmail);

                            if (isAssignedUser) {
                                // Chỉ cho phép chỉnh sửa status và nút Save
                                btnAssign.setVisibility(View.GONE);
                                btnClearAssign.setVisibility(View.GONE);
                                btnAddFile.setVisibility(View.GONE);
                                btnEdit.setVisibility(View.VISIBLE);
                                edtTaskTitle.setEnabled(false);
                                edtTaskDescription.setEnabled(false);
                                edtTaskDue.setEnabled(false);
                                spinnerTaskPriority.setEnabled(false);
                                spinnerTaskStatus.setEnabled(true);
                            } else {
                                // Tương tự VIEWER
                                btnAssign.setVisibility(View.GONE);
                                btnClearAssign.setVisibility(View.GONE);
                                btnAddFile.setVisibility(View.GONE);
                                btnEdit.setVisibility(View.GONE);
                                edtTaskTitle.setEnabled(false);
                                edtTaskDescription.setEnabled(false);
                                edtTaskDue.setEnabled(false);
                                spinnerTaskPriority.setEnabled(false);
                                spinnerTaskStatus.setEnabled(false);
                            }
                        } else {
                            Toast.makeText(TaskDetailActivity.this, "Lỗi khi lấy thông tin user!", Toast.LENGTH_SHORT).show();
                            applyViewerPermissions();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        Toast.makeText(TaskDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        applyViewerPermissions();
                    }
                });
                break;

            case ADMIN:
                // Toàn quyền
                btnAssign.setVisibility(View.VISIBLE);
                btnClearAssign.setVisibility(assignedEmail != null ? View.VISIBLE : View.GONE);
                btnAddFile.setVisibility(View.VISIBLE);
                btnEdit.setVisibility(View.VISIBLE);
                edtTaskTitle.setEnabled(true);
                edtTaskDescription.setEnabled(true);
                edtTaskDue.setEnabled(true);
                spinnerTaskPriority.setEnabled(true);
                spinnerTaskStatus.setEnabled(true);
                break;
        }
    }

    private void applyViewerPermissions() {
        btnAssign.setVisibility(View.GONE);
        btnClearAssign.setVisibility(View.GONE);
        btnAddFile.setVisibility(View.GONE);
        btnEdit.setVisibility(View.GONE);
        edtTaskTitle.setEnabled(false);
        edtTaskDescription.setEnabled(false);
        edtTaskDue.setEnabled(false);
        spinnerTaskPriority.setEnabled(false);
        spinnerTaskStatus.setEnabled(false);
    }

    private void anhxa() {
        edtTaskTitle = findViewById(R.id.edtTaskTitle);
        edtTaskDescription = findViewById(R.id.edtTaskDecript);
        edtTaskDue = findViewById(R.id.edtTaskDue);
        tvFileUrl = findViewById(R.id.tvFileUrl);
        spinnerTaskPriority = findViewById(R.id.spinnerTaskPriority);
        spinnerTaskStatus = findViewById(R.id.spinnerTaskStatus);
        tvCreatedOn = findViewById(R.id.tvCreatedOn);
        tvAssignedTo = findViewById(R.id.tvAssignedTo);
        btnEdit = findViewById(R.id.btnEdit);
        btnAddFile = findViewById(R.id.btnAddFile);
        btnAssign = findViewById(R.id.btnAssign);
        btnClearAssign = findViewById(R.id.btnClearAssign);
        imgTaskIcon = findViewById(R.id.imgTaskIcon);
    }

    private void setupButtonListeners() {
        btnAddFile.setOnClickListener(v -> openFilePicker());
        btnAssign.setOnClickListener(v -> showAssignMemberDialog());
        btnClearAssign.setOnClickListener(v -> {
            assignedEmail = null;
            updateAssignButtonText("None");
            btnClearAssign.setVisibility(View.GONE);
        });
        btnEdit.setOnClickListener(v -> {
            if (fileUri != null) {
                if (!isFileSupported(fileUri)) {
                    Toast.makeText(this, "Định dạng file không được hỗ trợ", Toast.LENGTH_SHORT).show();
                    return;
                }
                showProgress("Uploading file...");
                new UploadTask(this, fileUri, this).execute();
            } else {
                updateTask();
            }
        });
    }

    private void fetchTaskDetails(int taskId) {
        showProgress("Loading task details...");
        Call<Map<String, Object>> call = apiService.getTaskById(taskId);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> taskData = (Map<String, Object>) response.body().get("task");
                    if (taskData != null) {
                        currentTask = mapTask(taskData);
                        populateTaskDetails(currentTask);
                        applyRolePermissions();
                    } else {
                        Toast.makeText(TaskDetailActivity.this, "Không tìm thấy thông tin task!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(TaskDetailActivity.this, "Lỗi khi tải chi tiết task!", Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("FetchTaskError", "Response code: " + response.code() + ", body: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e("FetchTaskError", "Error reading error body: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                hideProgress();
                Toast.makeText(TaskDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FetchTaskFailure", "Error: " + t.toString());
            }
        });
    }

    private Task mapTask(Map<String, Object> taskMap) {
        Integer id = ((Number) taskMap.get("id")).intValue();
        String title = (String) taskMap.get("title");
        String description = (String) taskMap.get("description");
        String priority = (String) taskMap.get("priority");
        String status = (String) taskMap.get("status");
        String dueDateStr = (String) taskMap.get("dueDate");
        String fileUrl = (String) taskMap.get("fileUrl");
        String createdDateStr = (String) taskMap.get("createdDate");
        projectId = ((Number) taskMap.get("projectId")).intValue();

        LocalDate dueDate = LocalDate.parse(dueDateStr, isoDateFormatter);
        LocalDate createdDate = LocalDate.parse(createdDateStr, isoDateFormatter);

        User assignedUser = null;
        Map<String, Object> userMap = (Map<String, Object>) taskMap.get("user");
        if (userMap != null) {
            Integer userId = ((Number) userMap.get("id")).intValue();
            String userName = (String) userMap.get("name");
            String userEmail = (String) userMap.get("email");
            assignedUser = new User(userName, userEmail, userId);
        }

        return new Task(id, title, description, status, priority, assignedUser, dueDate, fileUrl, createdDateStr);
    }

    private void populateTaskDetails(Task task) {
        edtTaskTitle.setText(task.getTitle());
        edtTaskDescription.setText(task.getDescription());
        tvAssignedTo.setText("Assigned to: " + (task.getUser() != null && task.getUser().getEmail() != null ? task.getUser().getEmail() : "None"));
        edtTaskDue.setText(task.getDueDate().format(dateFormatter));
        tvFileUrl.setText(task.getFileUrl() != null ? task.getFileUrl() : "No file attached");
        tvCreatedOn.setText("Created on: " + task.getCreatedDate());

        ArrayAdapter<String> priorityAdapter = (ArrayAdapter<String>) spinnerTaskPriority.getAdapter();
        if (priorityAdapter != null && task.getPriority() != null) {
            int priorityPosition = priorityAdapter.getPosition(task.getPriority());
            spinnerTaskPriority.setSelection(priorityPosition >= 0 ? priorityPosition : 0);
        }

        ArrayAdapter<String> statusAdapter = (ArrayAdapter<String>) spinnerTaskStatus.getAdapter();
        if (statusAdapter != null && task.getStatus() != null) {
            int statusPosition = statusAdapter.getPosition(task.getStatus().toUpperCase());
            spinnerTaskStatus.setSelection(statusPosition >= 0 ? statusPosition : 0);
        }

        if (task.getUser() != null) {
            assignedEmail = task.getUser().getEmail();
            updateAssignButtonText(assignedEmail);
            btnClearAssign.setVisibility(View.VISIBLE);
        } else {
            assignedEmail = null;
            updateAssignButtonText("None");
            btnClearAssign.setVisibility(View.GONE);
        }
        if (task.getFileUrl() != null && URLUtil.isValidUrl(task.getFileUrl())) {
        tvFileUrl.setText("Tải file");
        tvFileUrl.setOnClickListener(v -> {
            try {
                String url = task.getFileUrl();
                String fileName = url.substring(url.lastIndexOf('/') + 1);

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setTitle("Đang tải file");
                request.setDescription("Đang tải tệp: " + fileName); // Mô tả chung và có tên file
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName); // Sử dụng tên file từ URL
                request.setAllowedOverMetered(true);
                request.setAllowedOverRoaming(true);

                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                if (downloadManager != null) {
                    downloadManager.enqueue(request);
                    Toast.makeText(TaskDetailActivity.this, "Đang tải file: " + fileName, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TaskDetailActivity.this, "Không thể khởi động DownloadManager", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(TaskDetailActivity.this, "Lỗi tải file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("DownloadFileError", "Error downloading file: " + e.getMessage());
            }
        });
    } else {
        tvFileUrl.setText("Không có tệp đính kèm");
        tvFileUrl.setOnClickListener(null);
    }

    }

    private void updateTask() {
        ProjectRole projectRole;
        try {
            projectRole = role != null ? ProjectRole.valueOf(role) : ProjectRole.VIEWER;
        } catch (IllegalArgumentException e) {
            projectRole = ProjectRole.VIEWER;
        }

        // Chỉ lấy các trường cần thiết dựa trên vai trò
        String status = spinnerTaskStatus.getSelectedItem() != null ? spinnerTaskStatus.getSelectedItem().toString() : "";
        UpdateTaskRequest updateRequest;

        if (projectRole == ProjectRole.MEMBER) {
            // MEMBER chỉ được cập nhật status
            updateRequest = new UpdateTaskRequest(
                    taskId,
                    currentTask.getTitle(),
                    currentTask.getDescription(),
                    status,
                    currentTask.getPriority(),
                    assignedEmail,
                    currentTask.getFileUrl(),
                    currentTask.getDueDate() != null ? currentTask.getDueDate().format(isoDateFormatter) : null
            );
        } else {
            // ADMIN hoặc fallback: lấy tất cả trường
            String title = edtTaskTitle.getText().toString().trim();
            String description = edtTaskDescription.getText().toString().trim();
            String priority = spinnerTaskPriority.getSelectedItem() != null ? spinnerTaskPriority.getSelectedItem().toString() : "";
            String dueDateStr = edtTaskDue.getText().toString().trim();

            if (title.isEmpty() || description.isEmpty() || dueDateStr.isEmpty()) {
                hideProgress();
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            LocalDate dueDateLocal;
            try {
                dueDateLocal = LocalDate.parse(dueDateStr, dateFormatter);
            } catch (Exception e) {
                hideProgress();
                Toast.makeText(this, "Định dạng ngày không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }
            String dueDateForBackend = dueDateLocal.format(isoDateFormatter);

            updateRequest = new UpdateTaskRequest(
                    taskId,
                    title,
                    description,
                    status,
                    priority,
                    assignedEmail,
                    fileUrl != null ? fileUrl : currentTask.getFileUrl(),
                    dueDateForBackend
            );
        }

        showProgress("Updating task...");
        Call<Map<String, Object>> call = apiService.updateTask(updateRequest);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(TaskDetailActivity.this, "Cập nhật task thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(TaskDetailActivity.this, "Lỗi khi cập nhật task!", Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("UpdateTaskError", "Response code: " + response.code() + ", body: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e("UpdateTaskError", "Error reading error body: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                hideProgress();
                Toast.makeText(TaskDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("UpdateTaskFailure", "Error: " + t.toString());
            }
        });
    }

    private void updateAssignButtonText(String email) {
        tvAssignedTo.setText("Assigned to: " + (email != null ? email : "None"));
    }

    private void loadPrioritySpinner() {
        String[] priorities = Arrays.stream(TaskPriority.values())
                .map(Enum::name)
                .toArray(String[]::new);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, priorities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTaskPriority.setAdapter(adapter);
    }

    private void loadStatusSpinner() {
        String[] statuses = Arrays.stream(TaskStatus.values())
                .map(Enum::name)
                .toArray(String[]::new);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statuses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTaskStatus.setAdapter(adapter);
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
            fileUrl = null; // Reset fileUrl khi chọn file mới
            Log.d("FilePicker", "Selected file: " + fileName + ", Uri: " + fileUri);
            tvFileUrl.setText("Selected file: " + fileName);
        }
    }

    private boolean isFileSupported(Uri uri) {
        String[] supportedExtensions = {"pdf", "doc", "docx", "txt", "ppt"};
        String fileName = getFileName(uri);
        if (fileName == null) {
            Log.e("FileSupport", "File name is null");
            return false;
        }
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        Log.d("FileSupport", "File extension: " + extension);
        for (String ext : supportedExtensions) {
            if (ext.equals(extension)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onTaskUploadSuccess(String uploadedFileUrl) {
        if (uploadedFileUrl != null && !uploadedFileUrl.isEmpty()) {
            fileUrl = uploadedFileUrl;
            Log.d("UploadTask", "File uploaded to Cloudinary, URL: " + fileUrl);
            updateTask();
        } else {
            hideProgress();
            Toast.makeText(this, "Tải file lên Cloudinary thất bại, URL rỗng!", Toast.LENGTH_SHORT).show();
            Log.e("UploadTaskError", "Uploaded file URL is null or empty");
        }
    }

    @Override
    public void onTaskUploadFailure(String errorMessage) {
        hideProgress();
        Toast.makeText(this, "Tải file thất bại: " + errorMessage, Toast.LENGTH_SHORT).show();
        Log.e("UploadTaskError", "Upload failed: " + errorMessage);
    }

    private void showAssignMemberDialog() {
        if (projectId == -1) {
            Toast.makeText(this, "Không tìm thấy ID dự án để tải danh sách thành viên!", Toast.LENGTH_SHORT).show();
            return;
        }

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_assign_member, null);
        dialog.setContentView(dialogView);
        RecyclerView recyclerMembers = dialogView.findViewById(R.id.recyclerMembers);
        ProgressBar progressBar = dialogView.findViewById(R.id.progressBar);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        recyclerMembers.setLayoutManager(new LinearLayoutManager(this));
        MemberAssignAdapter adapter = new MemberAssignAdapter(this, memberList, email -> {
            assignedEmail = email;
            updateAssignButtonText(email);
            btnClearAssign.setVisibility(View.VISIBLE);
            dialog.dismiss();
        });
        recyclerMembers.setAdapter(adapter);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        progressBar.setVisibility(View.VISIBLE);
        Call<Map<String, Object>> call = apiService.getAllMember(projectId);
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
                    Toast.makeText(TaskDetailActivity.this, "Không thể tải danh sách thành viên", Toast.LENGTH_SHORT).show();
                    Log.e("FetchMembersError", "Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(TaskDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FetchMembersFailure", "Error: " + t.toString());
            }
        });

        dialog.show();
    }

    private void pickDateEvent() {
        ProjectRole projectRole;
        try {
            projectRole = role != null ? ProjectRole.valueOf(role) : ProjectRole.VIEWER;
        } catch (IllegalArgumentException e) {
            projectRole = ProjectRole.VIEWER;
        }

        // Chỉ cho phép pick date nếu là ADMIN
        if (projectRole == ProjectRole.ADMIN) {
            edtTaskDue.setOnClickListener(v -> {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        this,
                        (view, yearOfDate, monthOfYear, dayOfMonth) -> {
                            LocalDate selectedDate = LocalDate.of(yearOfDate, monthOfYear + 1, dayOfMonth);
                            edtTaskDue.setText(selectedDate.format(dateFormatter));
                        },
                        year, month, day);
                datePickerDialog.show();
            });
        } else {
            edtTaskDue.setOnClickListener(null); // Vô hiệu hóa sự kiện click
        }
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

    private void showProgress(String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    void navigation() {
        ImageButton btnHome = findViewById(R.id.btnHome);
        ImageButton btnTask = findViewById(R.id.btnTask);
        ImageButton btnProfile = findViewById(R.id.btnProfile);
        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(this, HomePageActivity.class));
            overridePendingTransition(0, 0);
        });

        btnTask.setOnClickListener(v -> {
            startActivity(new Intent(this, TaskPageActivity.class));
            overridePendingTransition(0, 0);
        });

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            overridePendingTransition(0, 0);
        });
    }
}