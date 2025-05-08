package com.example.ui.Model;

import com.example.ui.Enum.TaskPriority;

import java.time.LocalDate;

public class Task {
    private Integer id;
    private String title;
    private String description;
    private String status;
    private TaskPriority priority;
    private User user;
    private LocalDate dueDate;
    private LocalDate createdDate;

    public Task(LocalDate dueDate, User user,  String status,  String title) {
        this.dueDate = dueDate;
        this.user = user;
        this.status = status;
        this.title = title;
    }

    public Task(Integer id, String title, String status, User user, LocalDate dueDate){
        this.id = id;
        this.title = title;
        this.status = status;
        this.user = user;
        this.dueDate = dueDate;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public User getUser() {
        return user;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }
}
