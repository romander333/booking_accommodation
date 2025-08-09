package com.romander.bookingapp.controller;

import static com.romander.bookingapp.util.BookingDataTest.getAnotherBookingRequestDto;
import static com.romander.bookingapp.util.BookingDataTest.getAnotherBookingResponseDto;
import static com.romander.bookingapp.util.BookingDataTest.getBookingResponseDto;
import static com.romander.bookingapp.util.BookingDataTest.getBookingResponseDtoForUpdateAndCreate;
import static com.romander.bookingapp.util.UserDataTest.getManagerSampleUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.romander.bookingapp.dto.booking.BookingRequestDto;
import com.romander.bookingapp.dto.booking.BookingResponseDto;
import com.romander.bookingapp.dto.booking.BookingUpdateStatusRequestDto;
import com.romander.bookingapp.model.Booking;
import com.romander.bookingapp.model.User;
import com.romander.bookingapp.service.NotificationService;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookingControllerTest {

    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private NotificationService notificationService;

    @BeforeAll
    void beforeAll() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void setUp(@Autowired DataSource dataSource) {
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/accommodation/add-accommodation.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/accommodation_amenities/add-accommodation_amenities.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/booking/add-booking.sql")
            );

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        User testUser = getManagerSampleUser();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                testUser,
                null,
                testUser.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void tearDown(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/booking/delete-booking.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/accommodation_amenities/remove-accommodation_amenities.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/accommodation/remove-accommodation.sql")
            );
        }
    }

    @Test
    @WithMockUser(username = "romander@gmail.com", roles = "CUSTOMER")
    @DisplayName("Create Booking when valid request is provided")
    void createBooking_WithValidRequest_ShouldReturnDto() throws Exception {
        BookingResponseDto expected = getBookingResponseDtoForUpdateAndCreate();

        BookingRequestDto requestDto = getAnotherBookingRequestDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/bookings")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        BookingResponseDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), BookingResponseDto.class);
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "createdAt"));
    }

    @Test
    @DisplayName("Get bookings by id and status when is provided valid data")
    void getBooking_WithValidId_ShouldReturnDto() throws Exception {
        Long userId = 2L;
        BookingResponseDto expected = getBookingResponseDto();
        List<BookingResponseDto> expectedList = new ArrayList<>();
        expectedList.add(expected);

        MvcResult result = mockMvc.perform(get("/bookings/{user_id}/status", userId)
                        .param("status", Booking.Status.PENDING.toString())
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode jsonNode = node.get("content");
        List<BookingResponseDto> actual = objectMapper
                .readerForListOf(BookingResponseDto.class)
                .readValue(jsonNode);

        assertEquals(expectedList.size(), actual.size());
        assertEquals(expectedList.get(0), actual.get(0));
    }

    @Test
    @DisplayName("Get bookings by current user")
    void getBookingsByCurrentUser_WithValidUSer_ShouldReturnDto() throws Exception {
        BookingResponseDto expected = getBookingResponseDto();
        BookingResponseDto expected2 = getAnotherBookingResponseDto();
        List<BookingResponseDto> expectedList = new ArrayList<>();
        expectedList.add(expected);
        expectedList.add(expected2);

        MvcResult result = mockMvc.perform(get("/bookings/my")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode jsonNode = node.get("content");
        List<BookingResponseDto> actual = objectMapper
                .readerForListOf(BookingResponseDto.class)
                .readValue(jsonNode);

        assertEquals(expectedList.size(), actual.size());
        assertEquals(expectedList.get(0), actual.get(0));

    }

    @Test
    @DisplayName("Get specific booking by id when valid id is provided")
    void getSpecificBooking_WithValidId_ShouldReturnDto() throws Exception {
        Long id = 1L;
        BookingResponseDto expected = getBookingResponseDto();

        MvcResult result = mockMvc.perform(get("/bookings/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookingResponseDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), BookingResponseDto.class);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("update booking by id when valid id is provided")
    void updateBooking_WithValidIdAndRequest_ShouldReturnDto() throws Exception {
        Long id = 2L;
        BookingResponseDto expected = getBookingResponseDtoForUpdateAndCreate();
        expected.setStatus(Booking.Status.CONFIRMED);
        expected.setId(id);
        BookingRequestDto requestDto = getAnotherBookingRequestDto();
        MvcResult result = mockMvc.perform(put("/bookings/{id}", id)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookingResponseDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), BookingResponseDto.class);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Update booking status by id and request when valid id and request is provided")
    void updateBookingStatus_WithValidRequest_Should() throws Exception {
        Long bookingId = 1L;
        Long userId = 2L;
        BookingResponseDto expected = getBookingResponseDto();
        expected.setStatus(Booking.Status.CONFIRMED);
        BookingUpdateStatusRequestDto requestDto = new BookingUpdateStatusRequestDto()
                .setStatus(Booking.Status.CONFIRMED);

        MvcResult result = mockMvc.perform(put("/bookings/{booking_id}/status", bookingId)
                        .param("userId", String.valueOf(userId))
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookingResponseDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), BookingResponseDto.class);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Delete booking by id when valid id is provided")
    void deleteBooking_WithValidId_ShouldReturnNoContentStatus() throws Exception {
        Long id = 2L;

        mockMvc.perform(delete("/bookings/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
