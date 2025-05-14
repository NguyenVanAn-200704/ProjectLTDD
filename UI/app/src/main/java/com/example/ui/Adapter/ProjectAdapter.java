package com.example.ui.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ui.Activity.ProjectDetailsActivity;
import com.example.ui.Model.Project;
import com.example.ui.R;
import com.example.ui.Retrofit.APIService;
import com.example.ui.Retrofit.RetrofitCilent;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class ProjectAdapter {

    private Context context;
    private List<Project> projectList;
    private LinearLayout container;
    private int userId;

    public ProjectAdapter(Context context, List<Project> projectList, LinearLayout container, int userId) {
        this.context = context;
        this.projectList = projectList;
        this.container = container;
        this.userId = userId;
    }

    public abstract void onProjectClick(Project project);

    public void loadProjects() {
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(context);

        for (Project project : projectList) {
            View itemView = inflater.inflate(R.layout.item_project, container, false);

            TextView tvName = itemView.findViewById(R.id.tvProjectName);
            TextView tvMember = itemView.findViewById(R.id.tvProjectMembers);
            ImageView ivDelete = itemView.findViewById(R.id.ivDelete);

            tvName.setText(project.getName());
            tvMember.setText(project.getMemberCount() + " members");

            // Chỉ hiển thị nút xóa nếu userId trùng với createBy
            if (project.getCreateBy() == userId) {
                ivDelete.setVisibility(View.VISIBLE);
            } else {
                ivDelete.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                int projectId = project.getId();
                int currentUserId = context.getSharedPreferences("UserPreferences", MODE_PRIVATE)
                        .getInt("userId", -1);

                if (currentUserId == -1) {
                    Toast.makeText(context, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                    return;
                }

                APIService apiService = RetrofitCilent.getRetrofit().create(APIService.class);
                Call<Map<String, Object>> call = apiService.getUserRole(projectId, currentUserId);

                call.enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Map<String, Object> data = response.body();
                            String role = (String) data.get("role");

                            Intent intent = new Intent(context, ProjectDetailsActivity.class);
                            intent.putExtra("projectId", project.getId());
                            intent.putExtra("projectName", project.getName());
                            intent.putExtra("projectCreatorId", project.getCreateBy());
                            intent.putExtra("role", role);
                            context.startActivity(intent);
                        } else {
                            Log.e("ProjectAdapter", "Get role failed: " + response.code());
                            Toast.makeText(context, "Không lấy được vai trò người dùng: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        Log.e("ProjectAdapter", "Get role error: " + t.getMessage());
                        Toast.makeText(context, "Lỗi kết nối đến server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });

            ivDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa dự án \"" + project.getName() + "\" không?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            APIService apiService = RetrofitCilent.getRetrofit().create(APIService.class);
                            Call<Map<String, Object>> call = apiService.deleteProject(project.getId());

                            call.enqueue(new Callback<Map<String, Object>>() {
                                @Override
                                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        Toast.makeText(context, "Đã xóa dự án: " + project.getName(), Toast.LENGTH_SHORT).show();
                                        projectList.remove(project);
                                        loadProjects();
                                    } else {
                                        Log.e("ProjectAdapter", "Delete project failed: " + response.code());
                                        Toast.makeText(context, "Xóa thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                                    Log.e("ProjectAdapter", "Delete project error: " + t.getMessage());
                                    Toast.makeText(context, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            });

            container.addView(itemView);
        }
    }
}