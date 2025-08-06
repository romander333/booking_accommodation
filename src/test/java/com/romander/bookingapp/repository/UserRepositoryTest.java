package com.romander.bookingapp.repository;

import static com.romander.bookingapp.util.UserDataTest.getSampleUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.romander.bookingapp.model.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Find user by email when valid email is prodvide")
    void findByEmail_WithValidEmail_ReturnsUser() {
        User expected = getSampleUser();
        String email = "romander@gmail.com";
        Optional<User> actual = userRepository.findByEmail(email);
        assertEquals(expected, actual.get());
    }

    @Test
    @DisplayName("Check if there is such a one by email when valid email is provided")
    void existsByEmail_WithUser_ShouldReturnBoolean() {
        User expected = getSampleUser();
        String email = expected.getEmail();
        assertTrue(userRepository.existsByEmail(email));
    }
}
