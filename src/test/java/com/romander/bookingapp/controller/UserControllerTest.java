package com.romander.bookingapp.controller;

import static com.romander.bookingapp.util.RoleDataTest.getRoleRequestDto;
import static com.romander.bookingapp.util.UserDataTest.getSampleUserProfileRequestDto;
import static com.romander.bookingapp.util.UserDataTest.getUpdateSampleUserResponseDto;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.romander.bookingapp.dto.user.RoleRequestDto;
import com.romander.bookingapp.dto.user.UserProfileRequestDto;
import com.romander.bookingapp.dto.user.UserResponseDto;
import com.romander.bookingapp.model.User;
import com.romander.bookingapp.repository.UserRepository;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerTest {
    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    void beforeAll(@Autowired WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void setUp(@Autowired DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/user/add-user.sql")
            );
        }
    }

    @AfterEach
    void tearDown(@Autowired DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/user/remove-user.sql")
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @WithMockUser(username = "manager", roles = "MANAGER")
    void updateRoleByUserId_WithValidId_ShouldSetRole() throws Exception {
        Long id = 3L;
        RoleRequestDto requestDto = getRoleRequestDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/users/{id}/role", id)
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        User user = userRepository.findById(id).orElseThrow();
        assertTrue(user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(requestDto.roleName())));
    }

    @Test
    @WithMockUser(username = "eva@gmail.com", roles = "CUSTOMER")
    void updateUser_WithValidRequest_ShouldReturnDto() throws Exception {
        UserProfileRequestDto requestDto = getSampleUserProfileRequestDto();
        UserResponseDto expected = getUpdateSampleUserResponseDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/users/me")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserResponseDto.class);

        assertEquals(expected, actual);
    }
}
