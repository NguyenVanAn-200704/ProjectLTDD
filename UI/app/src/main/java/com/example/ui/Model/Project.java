package com.example.ui.Model;

public class Project {
    private Integer id;
    private String name;
    private int memberCount;

    public Project(Integer id, String name, int memberCount) {
        this.id = id;
        this.name = name;
        this.memberCount = memberCount;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMemberCount() {
        return memberCount;
    }
}
