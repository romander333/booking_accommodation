package com.romander.bookingapp.util;

import static com.romander.bookingapp.util.UserDataTest.getSampleUser;

import com.romander.bookingapp.model.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

public class AuthenticationDataTest {

    public static Authentication getAuthentication() {
        User user = getSampleUser();
        return new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
    }
}
