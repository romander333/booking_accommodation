package com.romander.bookingapp.security;

import com.romander.bookingapp.dto.user.SignInRequestDto;
import com.romander.bookingapp.dto.user.UserSignInResponseDto;
import com.romander.bookingapp.model.User;
import com.romander.bookingapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtCore jwtCore;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public UserSignInResponseDto authenticate(SignInRequestDto signInRequestDto) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequestDto.getEmail(), signInRequestDto.getPassword())
        );
        String token = jwtCore.generateToken(authentication.getName());
        return new UserSignInResponseDto(token);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Authentication required");
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found by email: " + email));
    }
}
