package com.example.ui.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui.Activity.TaskDetailActivity;
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
        holder.tvAssign.setText(task.getUser() != null ? task.getUser().getName() : "None");
        holder.tvDueDate.setText(task.getDueDate().toString());


        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, TaskDetailActivity.class);
            intent.putExtra("taskId", task.getId());
            // Sử dụng startActivityForResult nếu context là Activity
            if (context instanceof Activity) {
                ((Activity) context).startActivityForResult(intent, 123);
            } else {
                context.startActivity(intent); // Fallback nếu không phải Activity
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}
