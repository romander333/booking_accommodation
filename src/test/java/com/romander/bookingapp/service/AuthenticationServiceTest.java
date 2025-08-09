package com.romander.bookingapp.service;

import static com.romander.bookingapp.util.AuthenticationDataTest.getAuthentication;
import static com.romander.bookingapp.util.UserDataTest.getSampleUser;
import static com.romander.bookingapp.util.UserDataTest.getSignInRequestDto;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.romander.bookingapp.dto.user.SignInRequestDto;
import com.romander.bookingapp.dto.user.UserSignInResponseDto;
import com.romander.bookingapp.model.User;
import com.romander.bookingapp.security.AuthenticationService;
import com.romander.bookingapp.security.JwtCore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {
    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private JwtCore jwtCore;

    @Mock
    private AuthenticationManager authenticationManager;

    @Test
    @DisplayName("Authenticate with request when valid request is provided")
    void authenticate_WithValidRequest_ShouldReturnDto() {
        SignInRequestDto requestDto = getSignInRequestDto();
        User user = getSampleUser();
        String token = jwtCore.generateToken(user.getUsername());
        Authentication authentication = getAuthentication();
        UserSignInResponseDto expected = new UserSignInResponseDto(token);

        when(jwtCore.generateToken(any())).thenReturn(token);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        UserSignInResponseDto actual = authenticationService.authenticate(requestDto);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get current user")
    void getCurrentUser_WithValidData_ShouldReturnUser() {
        User expected = getSampleUser();
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(expected);
        when(authentication.isAuthenticated()).thenReturn(true);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        User actual = authenticationService.getCurrentUser();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get current user")
    void getCurrentUser_WithNotAuthenticatedUser_ShouldThrowException() {
        Exception exception = assertThrows(RuntimeException.class,
                () -> authenticationService.getCurrentUser());

        assertEquals("Authentication required", exception.getMessage());
    }
}
