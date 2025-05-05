package com.example.project.Mapper;

import com.example.project.Entity.Task;
import com.example.project.Request.UpdateTaskRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UpdateTaskMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "project", ignore = true)
  @Mapping(target = "createdDate", ignore = true)
  void updateTaskFromRequest(UpdateTaskRequest updateTaskRequest, @MappingTarget Task task);
}
