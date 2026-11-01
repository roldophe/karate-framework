package com.example.karateframework.mapper;

import java.util.List;

import org.mapstruct.*;

import com.example.karateframework.dto.UserDTO;
import com.example.karateframework.model.User;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    UserDTO toDTO(User user);

    User toEntity(UserDTO userDTO);

    List<UserDTO> toDTOList(List<User> users);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(UserDTO dto, @MappingTarget User entity);
}
