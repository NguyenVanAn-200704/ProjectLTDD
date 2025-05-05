package com.example.project.Mapper;

import com.example.project.Entity.Task;
import com.example.project.Request.TaskRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "createdDate", ignore = true)
  Task taskRequestToTask(TaskRequest taskRequest);
}
