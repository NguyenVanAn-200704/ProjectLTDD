package com.example.ui.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ui.Activity.HomePageActivity;
import com.example.ui.Activity.LoginActivity;
import com.example.ui.Activity.ProjectDetailsActivity;
import com.example.ui.Model.Project;
import com.example.ui.R;

import java.util.List;

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

            tvName.setText(project.getName());
            tvMember.setText(project.getMemberCount() + " members");

            itemView.setOnClickListener(v->{
                int projectId = project.getId();
                context.getSharedPreferences("UserPreferences", MODE_PRIVATE)
                        .edit()
                        .putInt("projectId", projectId)
                        .apply();
                Intent intent = new Intent(context, ProjectDetailsActivity.class);
                intent.putExtra("projectName", project.getName());
                context.startActivity(intent);
            });
            container.addView(itemView);
        }
    }
}
