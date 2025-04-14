package com.example.ui.Model;

public class Project {
    private Integer id;
    private String name;
    private int memberCount;

    public Project(String name, int memberCount) {
        this.name = name;
        this.memberCount = memberCount;
    }

    public String getName() {
        return name;
    }

    public int getMemberCount() {
        return memberCount;
    }
}
