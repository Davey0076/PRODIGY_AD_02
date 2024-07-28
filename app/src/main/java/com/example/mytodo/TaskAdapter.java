package com.example.mytodo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private Context context;
    private OnDeleteClickListener onDeleteClickListener;
    private OnUpdateClickListener onUpdateClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(Task task);
    }

    public interface OnUpdateClickListener {
        void onUpdateClick(Task task);
    }

    public TaskAdapter(List<Task> taskList, Context context, OnDeleteClickListener onDeleteClickListener, OnUpdateClickListener onUpdateClickListener) {
        this.taskList = taskList;
        this.context = context;
        this.onDeleteClickListener = onDeleteClickListener;
        this.onUpdateClickListener = onUpdateClickListener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.tvTaskName.setText(task.getTaskName());
        holder.tvTaskDescription.setText(task.getTaskDescription());
        holder.tvTaskCategory.setText(task.getTaskCategory());

        // Convert task date and time to appropriate format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        String formattedDate = dateFormat.format(new Date(task.getTaskDate()));
        String formattedTime = timeFormat.format(new Date(task.getTaskTime()));

        holder.tvTaskDateTime.setText("Date: " + formattedDate + " Time: " + formattedTime);

        holder.btnDeleteTask.setOnClickListener(v -> onDeleteClickListener.onDeleteClick(task));
        holder.btnUpdateTask.setOnClickListener(v -> onUpdateClickListener.onUpdateClick(task));
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView tvTaskName, tvTaskDescription, tvTaskCategory, tvTaskDateTime;
        Button btnUpdateTask, btnDeleteTask;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskName = itemView.findViewById(R.id.tvTaskName);
            tvTaskDescription = itemView.findViewById(R.id.tvTaskDescription);
            tvTaskCategory = itemView.findViewById(R.id.tvTaskCategory);
            tvTaskDateTime = itemView.findViewById(R.id.tvTaskDateTime);
            btnUpdateTask = itemView.findViewById(R.id.btnUpdateTask);
            btnDeleteTask = itemView.findViewById(R.id.btnDeleteTask);
        }
    }
}
