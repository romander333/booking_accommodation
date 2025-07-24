package com.romander.bookingapp.repository;

import com.romander.bookingapp.dto.user.RoleRequestDto;
import com.romander.bookingapp.model.Role;
import com.romander.bookingapp.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "roles")
    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);
}
