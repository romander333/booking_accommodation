package com.romander.bookingapp.util;

import static com.romander.bookingapp.util.RoleDataTest.getRole;

import com.romander.bookingapp.model.Role;
import com.romander.bookingapp.model.User;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class UserDataTest {
    private final PasswordEncoder passwordEncoder;

    public static User sampleUser(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setEmail("romander@gmail.com");
        user.setFirstName("Roman");
        user.setLastName("Luch");
        user.setPassword("$2a$12$k5M6AyJ4itLQTb6KgNZsCeTROTmcRGE0AUr9Z0Kk/Mr9aDM8LPkYq");
        Role role = getRole();
        user.setRoles(Set.of(role));
        return user;
    }
}
