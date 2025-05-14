package com.example.ui.Model;

import java.time.LocalDate;

public class TaskTemp {
    private int id;
    private String title;
    private LocalDate dueDate;
    private String status;
    private String priority;

    public TaskTemp(int id, String title, LocalDate dueDate) {
        this.id = id;
        this.title = title;
        this.dueDate = dueDate;
        this.status = null; // Hoặc giá trị mặc định khác nếu cần
        this.priority = null; // Hoặc giá trị mặc định khác nếu cần
    }

    public TaskTemp(int id, String title, LocalDate dueDate, String status, String priority) {
        this.id = id;
        this.title = title;
        this.dueDate = dueDate;
        this.status = status;
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    // Các setter nếu cần
}