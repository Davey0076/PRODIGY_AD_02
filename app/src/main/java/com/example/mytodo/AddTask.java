package com.example.mytodo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AddTask extends AppCompatActivity {

    private EditText etTaskName, etTaskDescription, etTaskCategory;
    private TextView tvSelectedDate, tvSelectedTime;
    private Switch switchReminder;
    private Button btnSelectDate, btnSelectTime, btnSaveTask;
    private Calendar selectedDate, selectedTime;

    private FirebaseAuth auth;
    private DatabaseReference tasksDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        etTaskName = findViewById(R.id.etTaskName);
        etTaskDescription = findViewById(R.id.etTaskDescription);
        etTaskCategory = findViewById(R.id.etTaskCategory);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);
        switchReminder = findViewById(R.id.switchReminder);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        btnSaveTask = findViewById(R.id.btnSaveTask);

        auth = FirebaseAuth.getInstance();
        tasksDatabase = FirebaseDatabase.getInstance().getReference("Tasks").child(auth.getCurrentUser().getUid());

        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
            }
        });

        btnSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime();
            }
        });

        btnSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTask();
            }
        });
    }

    private void selectDate() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    tvSelectedDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void selectTime() {
        final Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    selectedTime = Calendar.getInstance();
                    selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedTime.set(Calendar.MINUTE, minute);
                    tvSelectedTime.setText(hourOfDay + ":" + String.format("%02d", minute));
                },
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    private void saveTask() {
        String taskName = etTaskName.getText().toString().trim();
        String taskDescription = etTaskDescription.getText().toString().trim();
        String taskCategory = etTaskCategory.getText().toString().trim();
        boolean setReminder = switchReminder.isChecked();

        if (taskName.isEmpty()) {
            etTaskName.setError("Task name is required");
            etTaskName.requestFocus();
            return;
        }

        if (taskDescription.isEmpty()) {
            etTaskDescription.setError("Task description is required");
            etTaskDescription.requestFocus();
            return;
        }

        if (taskCategory.isEmpty()) {
            etTaskCategory.setError("Task category is required");
            etTaskCategory.requestFocus();
            return;
        }

        if (selectedDate == null) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedTime == null) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
            return;
        }

        String taskId = tasksDatabase.push().getKey();
        Task task = new Task(taskId, taskName, taskDescription, taskCategory, selectedDate.getTimeInMillis(), selectedTime.getTimeInMillis(), setReminder);

        tasksDatabase.child(taskId).setValue(task)
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Toast.makeText(AddTask.this, "Task added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddTask.this, "Failed to add task", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
