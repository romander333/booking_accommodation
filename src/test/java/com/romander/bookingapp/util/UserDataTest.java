package com.romander.bookingapp.util;

import com.romander.bookingapp.exception.EntityNotFoundException;
import com.romander.bookingapp.model.Role;
import com.romander.bookingapp.model.User;
import com.romander.bookingapp.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static com.romander.bookingapp.util.RoleDataTest.getRole;

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
