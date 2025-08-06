package com.romander.bookingapp.controller;

import static com.romander.bookingapp.util.UserDataTest.getSampleSignUpRequestDto;
import static com.romander.bookingapp.util.UserDataTest.getSampleUserResponseDto;
import static com.romander.bookingapp.util.UserDataTest.getSignInRequestDto;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.romander.bookingapp.dto.user.SignInRequestDto;
import com.romander.bookingapp.dto.user.SignUpRequestDto;
import com.romander.bookingapp.dto.user.UserResponseDto;
import com.romander.bookingapp.dto.user.UserSignInResponseDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthControllerTest {
    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    void beforeAll(@Autowired WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Register user with request when valid request is provided")
    @Sql(scripts = "classpath:database/user/remove-user.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void registerUser_WithValidRequest_ShouldReturnDto() throws Exception {
        SignUpRequestDto requestDto = getSampleSignUpRequestDto();
        UserResponseDto expected = getSampleUserResponseDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(post("/auth/signup")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        UserResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserResponseDto.class);
        assertEquals(expected, actual);

    }

    @Test
    @DisplayName("Login user with request and expect token when valid request is provided")
    void login_WithValidRequest_ShouldReturnDto() throws Exception {
        SignInRequestDto requestDto = getSignInRequestDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/auth/signin")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserSignInResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserSignInResponseDto.class);
        assertNotNull(actual);
        assertFalse(actual.token().isEmpty());
    }
}
