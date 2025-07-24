package com.romander.bookingapp.service;

import com.romander.bookingapp.dto.user.RoleRequestDto;
import com.romander.bookingapp.dto.user.SignUpRequestDto;
import com.romander.bookingapp.dto.user.UserProfileRequestDto;
import com.romander.bookingapp.dto.user.UserResponseDto;
import jakarta.validation.Valid;

public interface UserService {
    UserResponseDto registerUser(SignUpRequestDto userRegistrationRequestDto);

    void updateRoleByUserId(Long userId, RoleRequestDto requestDto);

    UserResponseDto updateUser(UserProfileRequestDto requestDto);

    UserResponseDto getProfileInfo();
}
