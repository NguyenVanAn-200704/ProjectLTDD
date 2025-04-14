package com.example.ui.Request;

public class CreateProjectRequest {
    private final Integer createById = 1;
    private String name;
    private String description;

    public CreateProjectRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Integer getCreateById() {
        return createById;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
