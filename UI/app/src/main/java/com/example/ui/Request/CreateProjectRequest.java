package com.example.ui.Request;

import com.example.ui.Model.Member;
import java.util.List;

public class CreateProjectRequest {
    private int createById;
    private String name;
    private String description;
    private List<Member> members;

    public CreateProjectRequest(int userId, String name, String description, List<Member> members) {
        this.createById = userId;
        this.name = name;
        this.description = description;
        this.members = members;
    }

    public int getCreateById() {
        return createById;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Member> getMembers() {
        return members;
    }
}