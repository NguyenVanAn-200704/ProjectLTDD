package com.example.ui.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ui.Activity.HomePageActivity;
import com.example.ui.Activity.LoginActivity;
import com.example.ui.Activity.ProjectDetailsActivity;
import com.example.ui.Model.Project;
import com.example.ui.R;
import com.example.ui.Retrofit.APIService;
import com.example.ui.Retrofit.RetrofitCilent;

import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class ProjectAdapter {

    private Context context;
    private List<Project> projectList;
    private LinearLayout container;

    public ProjectAdapter(Context context, List<Project> projectList, LinearLayout container) {
        this.context = context;
        this.projectList = projectList;
        this.container = container;
    }

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

            itemView.setOnClickListener(v -> {
                int projectId = project.getId();
                context.getSharedPreferences("UserPreferences", MODE_PRIVATE)
                        .edit()
                        .putInt("projectId", projectId)
                        .apply();
                Intent intent = new Intent(context, ProjectDetailsActivity.class);
                intent.putExtra("projectName", project.getName());
                context.startActivity(intent);
            });

            ivDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa dự án \"" + project.getName() + "\" không?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            APIService apiService = RetrofitCilent.getRetrofit().create(APIService.class);
                            Call<Map<String, Object>> call = apiService.deleteProject(project.getId());

                            call.enqueue(new retrofit2.Callback<Map<String, Object>>() {
                                @Override
                                public void onResponse(Call<Map<String, Object>> call, retrofit2.Response<Map<String, Object>> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        Toast.makeText(context, "Đã xóa dự án: " + project.getName(), Toast.LENGTH_SHORT).show();
                                        projectList.remove(project);
                                        loadProjects(); // reload lại giao diện
                                    } else {
                                        Toast.makeText(context, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                                    Toast.makeText(context, "Lỗi kết nối API!", Toast.LENGTH_SHORT).show();
                                    t.printStackTrace();
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
