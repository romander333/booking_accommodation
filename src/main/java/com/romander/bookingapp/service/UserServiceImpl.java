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
import java.util.Set;

import com.romander.bookingapp.security.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public UserResponseDto registerUser(SignUpRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("This email as"
                    + requestDto.getEmail()
                    + " is already in use");
        }
        User newUser = userMapper.toModel(requestDto);
        newUser.setEmail(requestDto.getEmail());
        newUser.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        newUser.setFirstName(requestDto.getFirstName());
        newUser.setLastName(requestDto.getLastName());
        Role role = roleRepository.findByName(Role.RoleName.CUSTOMER)
                .orElseThrow(() -> new EntityNotFoundException("Role not found by name: " + Role.RoleName.CUSTOMER));
        newUser.setRoles(Set.of(role));
        userRepository.save(newUser);

        return userMapper.toDto(newUser);
    }

    @Override
    @Transactional
    public void updateRoleByUserId(Long userId, RoleRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found by id" + userId));

        Role role = roleRepository.findByName(requestDto.roleName())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Role not found by name: " + Role.RoleName.CUSTOMER));

        user.setRoles(Set.of(role));
    }


    @Override
    public UserResponseDto updateUser(UserProfileRequestDto requestDto) {
        User user = getCurrentUser();
        userMapper.updateUser(user, requestDto);
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto getProfileInfo() {
        User user = getCurrentUser();
        return userMapper.toDto(user);
    }

    private User getCurrentUser() {
        return authenticationService.getCurrentUser();
    }

}
