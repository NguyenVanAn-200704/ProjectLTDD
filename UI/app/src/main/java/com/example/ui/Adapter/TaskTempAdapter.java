package com.example.ui.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui.Activity.TaskDetailActivity;
import com.example.ui.Model.TaskTemp;
import com.example.ui.R;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class TaskTempAdapter extends RecyclerView.Adapter<TaskTempAdapter.TaskViewHolder> {

    private Context context;
    private List<TaskTemp> taskList;
    private OnTaskClickListener listener;

    private String role; // Thêm trường role
    private int userId;

    public interface OnTaskClickListener {
        void onTaskClick(int taskId);
    }

    public TaskTempAdapter(Context context, List<TaskTemp> taskList, OnTaskClickListener listener) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskTemp task = taskList.get(position);
        holder.tvTitle.setText(task.getTitle());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        holder.tvDueDate.setText("Due at: " + task.getDueDate().format(formatter));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskClick(task.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle, tvDueDate;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTaskName);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
        }
    }
}