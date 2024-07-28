package com.example.mytodo;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTask extends AppCompatActivity {

    private EditText etTaskName, etTaskDescription, etTaskCategory;
    private TextView tvSelectedDate, tvSelectedTime;
    private Switch switchReminder;
    private Button btnSelectDate, btnSelectTime, btnSaveTask;
    private Calendar selectedDate, selectedTime;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private String taskId;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

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
        firestore = FirebaseFirestore.getInstance();

        btnSelectDate.setOnClickListener(v -> selectDate());
        btnSelectTime.setOnClickListener(v -> selectTime());
        btnSaveTask.setOnClickListener(v -> saveTask());

        // Check if this activity was started with task data
        if (getIntent().hasExtra("taskId")) {
            taskId = getIntent().getStringExtra("taskId");
            String taskName = getIntent().getStringExtra("taskName");
            String taskDescription = getIntent().getStringExtra("taskDescription");
            String taskCategory = getIntent().getStringExtra("taskCategory");
            long taskDate = getIntent().getLongExtra("taskDate", 0);
            long taskTime = getIntent().getLongExtra("taskTime", 0);
            boolean taskReminder = getIntent().getBooleanExtra("taskReminder", false);

            // Populate the fields with the existing task data
            etTaskName.setText(taskName);
            etTaskDescription.setText(taskDescription);
            etTaskCategory.setText(taskCategory);
            switchReminder.setChecked(taskReminder);

            // Set the selected date and time
            selectedDate = Calendar.getInstance();
            selectedDate.setTimeInMillis(taskDate);
            tvSelectedDate.setText(dateFormat.format(selectedDate.getTime()));

            selectedTime = Calendar.getInstance();
            selectedTime.setTimeInMillis(taskTime);
            tvSelectedTime.setText(timeFormat.format(selectedTime.getTime()));
        }
    }

    private void selectDate() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    tvSelectedDate.setText(dateFormat.format(selectedDate.getTime()));
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
                    tvSelectedTime.setText(timeFormat.format(selectedTime.getTime()));
                },
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    private void saveTask() {
        String taskName = etTaskName.getText().toString().trim();
        String taskDescription = etTaskDescription.getText().toString().trim();
        String taskCategory = etTaskCategory.getText().toString().trim();
        boolean setReminder = switchReminder.isChecked();

        if (TextUtils.isEmpty(taskName)) {
            etTaskName.setError("Task name is required");
            etTaskName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(taskDescription)) {
            etTaskDescription.setError("Task description is required");
            etTaskDescription.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(taskCategory)) {
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

        String userId = auth.getCurrentUser().getUid();
        if (taskId == null) {
            taskId = firestore.collection("users").document(userId).collection("tasks").document().getId();
        }

        Task task = new Task(taskId, taskName, taskDescription, taskCategory, selectedDate.getTimeInMillis(), selectedTime.getTimeInMillis(), setReminder);

        firestore.collection("users").document(userId).collection("tasks").document(taskId)
                .set(task)
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        if (setReminder) {
                            setTaskReminder(task);
                        }
                        Toast.makeText(AddTask.this, "Task saved successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddTask.this, HomePage.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(AddTask.this, "Failed to save task", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setTaskReminder(Task task) {
        long reminderTime = task.getTaskTime(); // Use taskDate or taskTime depending on your requirement

        Intent intent = new Intent(this, TaskAlarmReceiver.class);
        intent.putExtra("taskName", task.getTaskName());
        intent.putExtra("taskDescription", task.getTaskDescription());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, task.getTaskId().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
        }
    }
}
