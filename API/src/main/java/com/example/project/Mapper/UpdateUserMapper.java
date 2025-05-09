package com.example.project.Mapper;

import com.example.project.Entity.User;
import com.example.project.Request.UpdateUserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UpdateUserMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "email", ignore = true)
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "projects", ignore = true)
  @Mapping(target = "projectMembers", ignore = true)
  @Mapping(target = "tasks", ignore = true)
  void updateUserFromProfile(UpdateUserRequest updateUserRequest, @MappingTarget User user);
}
