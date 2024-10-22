package com.scheduler;

import java.time.LocalDateTime;

public class Task {
    private int index;
    private String title;
    private String description;
    private int priority;
    private LocalDateTime deadline;
    private String status; // "Pending", "Completed", "Overdue"
    
    // Constructor
    public Task(int index,String title, String description, int priority, LocalDateTime deadline) {
        this.index=index;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.deadline = deadline;
        this.status = "Pending"; // Default status
    }
    //constructor for updating status
    public Task(int index,String title, String description, int priority, LocalDateTime deadline,String status) {
        this.index=index;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.deadline = deadline;
        this.status = status; // status for updates
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public int getindex() {
        return index;
    }
    public void setindex(int index) {
        this.index=index;
    }
    @Override
    public String toString() {
        return "Task "+index +":"+
                "\nTitle:\n" + title +
                "\nDescription:\n" + description +
                "Priority:\n" + priority +
                "\nDeadline:\n" + deadline +
                "\nStatus:\n" + status;
    }
}