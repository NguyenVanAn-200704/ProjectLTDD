package com.example.project.Mapper;

import com.example.project.Entity.User;
import com.example.project.Request.UserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "projectMembers", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    User userRequestToUser(UserRequest userRequest);
}

