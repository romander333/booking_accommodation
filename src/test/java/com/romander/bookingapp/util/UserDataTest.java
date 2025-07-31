package com.romander.bookingapp.util;

import static com.romander.bookingapp.util.RoleDataTest.getRole;

import com.romander.bookingapp.dto.user.SignUpRequestDto;
import com.romander.bookingapp.dto.user.UserProfileRequestDto;
import com.romander.bookingapp.dto.user.UserResponseDto;
import com.romander.bookingapp.model.Role;
import com.romander.bookingapp.model.User;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class UserDataTest {
    private final PasswordEncoder passwordEncoder;

    public static User getSampleUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("romander@gmail.com");
        user.setFirstName("Roman");
        user.setLastName("Luch");
        user.setPassword("$2a$12$k5M6AyJ4itLQTb6KgNZsCeTROTmcRGE0AUr9Z0Kk/Mr9aDM8LPkYq");
        Role role = getRole();
        user.setRoles(Set.of(role));
        return user;
    }

    public static User createSampleUser() {
        Role role = getRole();
        User user = new User();
        user.setId(3L);
        user.setEmail("eva@gmail.com");
        user.setPassword("password123");
        user.setFirstName("Eva");
        user.setLastName("Gavin");
        user.setRoles(Set.of(role));
        return user;
    }

    public static UserResponseDto getSampleUserResponseDto() {
        return new UserResponseDto(
                "eva@gmail.com",
                "Eva",
                "Gavin");
    }

    public static UserResponseDto getUpdateSampleUserResponseDto() {
        return new UserResponseDto(
                "Eva@gmail.com",
                "eva",
                "Gaviley");
    }

    public static SignUpRequestDto getSampleSignUpRequestDto() {
        return new SignUpRequestDto()
                .setEmail("eva@gmail.com")
                .setPassword("password123")
                .setConfirmPassword("password123")
                .setFirstName("Eva")
                .setLastName("Gavin");
    }

    public static SignUpRequestDto getExistSampleSignUpRequestDto() {
        return new SignUpRequestDto()
                .setEmail("romander@gmail.com")
                .setPassword("password123")
                .setConfirmPassword("password123")
                .setFirstName("Roman")
                .setLastName("Luch");
    }
}
