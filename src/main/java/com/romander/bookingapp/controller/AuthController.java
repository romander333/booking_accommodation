package com.romander.bookingapp.controller;

import com.romander.bookingapp.dto.user.SignInRequestDto;
import com.romander.bookingapp.dto.user.SignUpRequestDto;
import com.romander.bookingapp.dto.user.UserResponseDto;
import com.romander.bookingapp.dto.user.UserSignInResponseDto;
import com.romander.bookingapp.security.AuthenticationService;
import com.romander.bookingapp.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User authentication", description = "Endpoint for authentication user")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/signup")
    public UserResponseDto registration(@RequestBody @Valid SignUpRequestDto requestDto) {
        return userService.registerUser(requestDto);
    }

    @PostMapping("/signin")
    public UserSignInResponseDto login(@RequestBody SignInRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }

}
