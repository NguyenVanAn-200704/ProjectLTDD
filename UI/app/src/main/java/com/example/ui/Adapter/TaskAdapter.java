package com.example.ui.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui.Model.Task;
import com.example.ui.R;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvStatus, tvAssign, tvDueDate;

        public TaskViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvStatus = itemView.findViewById(R.id.tvTaskStatus);
            tvAssign = itemView.findViewById(R.id.tvTaskAssign);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task2, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.tvTitle.setText(task.getTitle());
        holder.tvStatus.setText(task.getStatus());
        holder.tvAssign.setText(task.getUser().getName());
        holder.tvDueDate.setText(task.getDueDate().toString());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}
