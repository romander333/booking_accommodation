package com.romander.bookingapp.controller;

import com.romander.bookingapp.dto.user.RoleRequestDto;
import com.romander.bookingapp.dto.user.UserProfileRequestDto;
import com.romander.bookingapp.dto.user.UserResponseDto;
import com.romander.bookingapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User management", description = "Endpoint for managing user")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Update role", description = "Update role by id")
    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}/role")
    public void updateRole(@PathVariable Long id, @RequestBody RoleRequestDto requestDto) {
        userService.updateRoleByUserId(id, requestDto);
    }

    @Operation(summary = "Update profile", description = "Update current user profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/me")
    public UserResponseDto updateProfile(@RequestBody @Valid UserProfileRequestDto requestDto) {
        return userService.updateUser(requestDto);
    }
}

