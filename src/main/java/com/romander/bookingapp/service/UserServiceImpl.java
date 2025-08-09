package com.romander.bookingapp.service;

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
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    private final UserMapper userMapper;

    private final AuthenticationService authenticationService;

    @Override
    public UserResponseDto registerUser(SignUpRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("This email as"
                    + requestDto.getEmail()
                    + " is already in use");
        }
        User newUser = userMapper.toModel(requestDto);
        newUser.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        Role role = roleRepository.findByName(Role.RoleName.CUSTOMER)
                .orElseThrow(() -> new EntityNotFoundException("Role not found by name: "
                        + Role.RoleName.CUSTOMER));

        newUser.setRoles(Set.of(role));
        userRepository.save(newUser);
        return userMapper.toDto(newUser);
    }

    @Override
    public void updateRoleByUserId(Long userId, RoleRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found by id" + userId));

        Role role = roleRepository.findByName(requestDto.roleName())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Role not found by name: " + requestDto.roleName()));
        user.setRoles(Set.of(role));
    }

    @Override
    public UserResponseDto updateUser(UserProfileRequestDto requestDto) {
        User user = getCurrentUser();
        userMapper.updateUser(user, requestDto);
        return userMapper.toDto(user);
    }

    private User getCurrentUser() {
        return authenticationService.getCurrentUser();
    }
}
