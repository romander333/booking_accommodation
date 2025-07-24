package com.romander.bookingapp.controller;

import com.romander.bookingapp.dto.user.RoleRequestDto;
import com.romander.bookingapp.dto.user.UserProfileRequestDto;
import com.romander.bookingapp.dto.user.UserResponseDto;
import com.romander.bookingapp.model.Role;
import com.romander.bookingapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('CUSTOMER')")
   @GetMapping("/me")
   public UserResponseDto userAccess() {
        return userService.getProfileInfo();
   }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}/role")
    public void updateRole(@PathVariable Long id, @RequestBody RoleRequestDto requestDto) {
        userService.updateRoleByUserId(id, requestDto);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/me")
    public UserResponseDto updateProfile(@RequestBody @Valid UserProfileRequestDto requestDto) {
        return userService.updateUser(requestDto);
    }
}

