package com.example.project.Mapper;

import com.example.project.Entity.User;
import com.example.project.Request.UserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "projectMembers", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    User userRequestToUser(UserRequest userRequest);
}

