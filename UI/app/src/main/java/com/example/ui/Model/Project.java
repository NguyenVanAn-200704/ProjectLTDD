package com.example.ui.Model;

public class Project {
    private Integer id;
    private String name;
    private int memberCount;

    private Integer createBy;

    public Project(Integer id,Integer createBy,  String name, int memberCount) {
        this.id = id;
        this.name = name;
        this.createBy = createBy;
        this.memberCount = memberCount;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getCreateBy(){return  createBy;}

    public int getMemberCount() {
        return memberCount;
    }
}
