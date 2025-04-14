package com.example.project.Mapper;

import com.example.project.Entity.Project;
import com.example.project.Request.ProjectRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "projectMembers", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    Project projectRequestToProject(ProjectRequest projectRequest);
}
