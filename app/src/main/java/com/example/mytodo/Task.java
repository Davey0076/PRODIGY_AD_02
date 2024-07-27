package com.example.mytodo;

public class Task {
    private String taskId;
    private String taskName;
    private String taskDescription;
    private String taskCategory;
    private long taskDate;
    private long taskTime;
    private boolean taskReminder;

    // Default constructor required for calls to DataSnapshot.getValue(Task.class)
    public Task() {
    }

    public Task(String taskId, String taskName, String taskDescription, String taskCategory, long taskDate, long taskTime, boolean taskReminder) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskCategory = taskCategory;
        this.taskDate = taskDate;
        this.taskTime = taskTime;
        this.taskReminder = taskReminder;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getTaskCategory() {
        return taskCategory;
    }

    public void setTaskCategory(String taskCategory) {
        this.taskCategory = taskCategory;
    }

    public long getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(long taskDate) {
        this.taskDate = taskDate;
    }

    public long getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(long taskTime) {
        this.taskTime = taskTime;
    }

    public boolean isTaskReminder() {
        return taskReminder;
    }

    public void setTaskReminder(boolean taskReminder) {
        this.taskReminder = taskReminder;
    }
}
