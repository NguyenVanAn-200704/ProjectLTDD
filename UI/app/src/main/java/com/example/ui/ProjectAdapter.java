package com.example.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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

            container.addView(itemView);
        }
    }
}
