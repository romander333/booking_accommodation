package com.romander.bookingapp.util;

import com.romander.bookingapp.model.Role;

public class RoleDataTest {

    public static Role getRole() {
        Role role = new Role();
        role.setId(1L);
        role.setName(Role.RoleName.CUSTOMER);
        return role;
    }
}
