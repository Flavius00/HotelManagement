package com.hotelchain.usermanagement.mapper;

import com.hotelchain.usermanagement.dto.UserDTO;
import com.hotelchain.usermanagement.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "notificationPreferences", ignore = true)
    User toEntity(UserDTO userDTO);
}