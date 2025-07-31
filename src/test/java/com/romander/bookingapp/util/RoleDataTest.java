package com.romander.bookingapp.util;

import com.romander.bookingapp.dto.user.RoleRequestDto;
import com.romander.bookingapp.model.Role;

public class RoleDataTest {

    public static Role getRole() {
        Role role = new Role();
        role.setId(1L);
        role.setName(Role.RoleName.CUSTOMER);
        return role;
    }

    public static Role getAnotherRole() {
        Role role = new Role();
        role.setId(2L);
        role.setName(Role.RoleName.MANAGER);
        return role;
    }

    public static RoleRequestDto getRoleRequestDto() {
        return new RoleRequestDto(Role.RoleName.MANAGER);
    }
}
