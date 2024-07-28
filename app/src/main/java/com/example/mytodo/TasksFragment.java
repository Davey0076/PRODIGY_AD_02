package com.example.mytodo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private LinearLayout layoutNoTasks;
    private LinearLayout layoutTasks;
    private FloatingActionButton btnAddTask;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        recyclerView = view.findViewById(R.id.recyclerTasks);
        layoutNoTasks = view.findViewById(R.id.layoutNoTasks);
        layoutTasks = view.findViewById(R.id.layoutTasks);
        btnAddTask = view.findViewById(R.id.btnAddTask);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList, getContext(), this::deleteTask, this::updateTask);
        recyclerView.setAdapter(taskAdapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        loadTasks();

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addtasks = new Intent(getActivity(), AddTask.class);
                startActivity(addtasks);
            }
        });

        return view;
    }

    private void loadTasks() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            CollectionReference tasksRef = db.collection("users").document(currentUser.getUid()).collection("tasks");
            tasksRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Task taskItem = document.toObject(Task.class);
                            taskList.add(taskItem);
                        }
                        taskAdapter.notifyDataSetChanged();
                        layoutNoTasks.setVisibility(View.GONE);
                        layoutTasks.setVisibility(View.VISIBLE);
                    } else {
                        layoutNoTasks.setVisibility(View.VISIBLE);
                        layoutTasks.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    private void deleteTask(Task task) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid())
                    .collection("tasks").document(task.getTaskId())
                    .delete().addOnSuccessListener(aVoid -> {
                        taskList.remove(task);
                        taskAdapter.notifyDataSetChanged();
                        if (taskList.isEmpty()) {
                            layoutNoTasks.setVisibility(View.VISIBLE);
                            layoutTasks.setVisibility(View.GONE);
                        }
                    });
        }
    }

    private void updateTask(Task task) {
        Intent intent = new Intent(getActivity(), AddTask.class);
        intent.putExtra("taskId", task.getTaskId());
        intent.putExtra("taskName", task.getTaskName());
        intent.putExtra("taskDescription", task.getTaskDescription());
        intent.putExtra("taskCategory", task.getTaskCategory());
        intent.putExtra("taskDate", task.getTaskDate());
        intent.putExtra("taskTime", task.getTaskTime());
        intent.putExtra("taskReminder", task.isTaskReminder());
        startActivity(intent);
    }
}
