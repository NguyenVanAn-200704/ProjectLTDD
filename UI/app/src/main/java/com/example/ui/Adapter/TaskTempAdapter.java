package com.example.ui.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ui.Model.TaskTemp;
import com.example.ui.R;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class TaskTempAdapter {
    private Context context;
    private List<TaskTemp> taskList;
    private LinearLayout container;

    public TaskTempAdapter(Context context, List<TaskTemp> taskList, LinearLayout container) {
        this.context = context;
        this.taskList = taskList;
        this.container = container;
    }
    public void loadTasks() {
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Formatter để định dạng ngày
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (TaskTemp task : taskList) {
            View itemView = inflater.inflate(R.layout.item_task, container, false);

            TextView tvName = itemView.findViewById(R.id.tvTaskName);
            TextView tvMember = itemView.findViewById(R.id.tvDueDate);

            tvName.setText(task.getTitle());

            // Convert LocalDate -> String
            String formattedDate = task.getDueDate().format(formatter);
            tvMember.setText("Due at: " + formattedDate);

            container.addView(itemView);
        }
    }
}
