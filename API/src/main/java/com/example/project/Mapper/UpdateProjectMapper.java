package com.example.project.Mapper;

import com.example.project.Entity.Project;
import com.example.project.Request.UpdateProjectRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UpdateProjectMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "projectMembers", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    void updateProjectFromRequest(UpdateProjectRequest updateProjectRequest, @MappingTarget Project project);
}
