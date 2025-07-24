package com.romander.bookingapp.dto.user;

import com.romander.bookingapp.model.Role;

public record RoleRequestDto(Role.RoleName roleName) {
}
