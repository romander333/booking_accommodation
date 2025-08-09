package com.romander.bookingapp.service;

import static com.romander.bookingapp.util.RoleDataTest.getAnotherRole;
import static com.romander.bookingapp.util.RoleDataTest.getRole;
import static com.romander.bookingapp.util.RoleDataTest.getRoleRequestDto;
import static com.romander.bookingapp.util.UserDataTest.createSampleUser;
import static com.romander.bookingapp.util.UserDataTest.getExistSampleSignUpRequestDto;
import static com.romander.bookingapp.util.UserDataTest.getSampleSignUpRequestDto;
import static com.romander.bookingapp.util.UserDataTest.getSampleUserProfileRequestDto;
import static com.romander.bookingapp.util.UserDataTest.getSampleUserResponseDto;
import static com.romander.bookingapp.util.UserDataTest.getUpdateSampleUserResponseDto;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.romander.bookingapp.dto.user.RoleRequestDto;
import com.romander.bookingapp.dto.user.SignUpRequestDto;
import com.romander.bookingapp.dto.user.UserProfileRequestDto;
import com.romander.bookingapp.dto.user.UserResponseDto;
import com.romander.bookingapp.exception.EntityNotFoundException;
import com.romander.bookingapp.exception.RegistrationException;
import com.romander.bookingapp.mapper.UserMapper;
import com.romander.bookingapp.model.Role;
import com.romander.bookingapp.model.User;
import com.romander.bookingapp.repository.RoleRepository;
import com.romander.bookingapp.repository.UserRepository;
import com.romander.bookingapp.security.AuthenticationService;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("Register user with request when valid request is provided")
    void registerUser_WithValidRequest_ShouldReturnDto() {
        String email = "eva@gmail.com";
        User user = createSampleUser();
        String encode = "password123";
        User savedUser = createSampleUser();
        savedUser.setPassword(encode);
        Role role = getRole();
        UserResponseDto expected = getSampleUserResponseDto();
        SignUpRequestDto requestDto = getSampleSignUpRequestDto();

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn(encode);
        when(roleRepository.findByName(Role.RoleName.CUSTOMER)).thenReturn(Optional.of(role));
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(expected);

        UserResponseDto actual = userService.registerUser(requestDto);

        assertEquals(expected, actual);

    }

    @Test
    @DisplayName("Register user with request when Invalid request is provided")
    void registerUser_WithExistEmail_ShouldReturnDto() {
        SignUpRequestDto requestDto = getExistSampleSignUpRequestDto();

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

        Exception exception = assertThrows(RegistrationException.class,
                () -> userService.registerUser(requestDto));

        assertEquals("This email as" + requestDto.getEmail() + " is already in use",
                exception.getMessage());
    }

    @Test
    @DisplayName("Update users role with invalid id")
    void updateRoleByUserId_WithInvalidId_ShouldThrowEntityNotFoundException() {
        Long userId = -10L;
        RoleRequestDto requestDto = getRoleRequestDto();

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> userService.updateRoleByUserId(userId, requestDto));
        assertEquals("User not found by id" + userId, exception.getMessage());
    }

    @Test
    @DisplayName("Update users role with id when valid id is provided")
    void updateRoleByUserId_WithValidId_ShouldSetRole() {
        Long userId = 1L;
        Role role = getAnotherRole();
        User expected = createSampleUser();
        expected.setRoles(Set.of(role));
        RoleRequestDto requestDto = getRoleRequestDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(expected));
        when(roleRepository.findByName(role.getName())).thenReturn(Optional.of(role));

        userService.updateRoleByUserId(userId, requestDto);

        Optional<User> actual = userRepository.findById(userId);

        assertEquals(expected, actual.get());
    }

    @Test
    @DisplayName("Update user with request when valid request is provided")
    void updateUser_WithValidRequest_ShouldReturnDto() {
        UserResponseDto expected = getUpdateSampleUserResponseDto();
        User user = createSampleUser();
        UserProfileRequestDto requestDto = getSampleUserProfileRequestDto();
        when(authenticationService.getCurrentUser()).thenReturn(user);
        doNothing().when(userMapper).updateUser(user, requestDto);
        when(userMapper.toDto(user)).thenReturn(expected);

        UserResponseDto actual = userService.updateUser(requestDto);
        assertEquals(expected, actual);
    }
}
