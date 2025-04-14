package com.example.ui.Model;

import java.time.LocalDate;

public class TaskTemp {
    private String title;
    private LocalDate dueDate;

    public TaskTemp(String title, LocalDate dueDate) {
        this.title = title;
        this.dueDate = dueDate;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }
}
