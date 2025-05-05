package com.example.ui.Request;

public class CreateProjectRequest {
    private Integer createById;
    private String name;
    private String description;

    public CreateProjectRequest(Integer createById, String name, String description) {
        this.createById = createById;
        this.name = name;
        this.description = description;
    }

    public Integer getCreateById() {
        return createById;
    }

    public void setCreateById(Integer createById) {
        this.createById = createById;
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
