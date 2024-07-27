package com.example.mytodo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CalendarFragment extends Fragment {

    private CalendarView calendarView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Set<String> taskDates;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        taskDates = new HashSet<>();

        loadTasks();

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            // Handle date selection if needed
            String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            if (taskDates.contains(selectedDate)) {
                // Display tasks for the selected date
            }
        });

        return view;
    }

    private void loadTasks() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("tasks")
                    .whereEqualTo("userId", user.getUid())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Task task = document.toObject(Task.class);
                            String dateStr = sdf.format(task.getTaskDate());
                            taskDates.add(dateStr);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle the error
                    });
        }
    }
}
