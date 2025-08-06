package com.romander.bookingapp.mapper;

import com.romander.bookingapp.config.MapperConfig;
import com.romander.bookingapp.dto.user.SignUpRequestDto;
import com.romander.bookingapp.dto.user.UserProfileRequestDto;
import com.romander.bookingapp.dto.user.UserResponseDto;
import com.romander.bookingapp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {

    void updateUser(@MappingTarget User user, UserProfileRequestDto requestDto);

    User toModel(SignUpRequestDto requestDto);

    UserResponseDto toDto(User user);
}
