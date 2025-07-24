package com.romander.bookingapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.romander.bookingapp.dto.booking.BookingRequestDto;
import com.romander.bookingapp.dto.booking.BookingResponseDto;
import com.romander.bookingapp.dto.booking.BookingUpdateStatusRequestDto;
import com.romander.bookingapp.model.Booking;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.romander.bookingapp.util.BookingDataTest.getBookingResponseDto;
import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookingControllerTest {

    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext webApplicationContext) {
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
                    new ClassPathResource("database/accommodation_amenities/add-accommodation_amenities.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/booking/add-booking.sql")
            );

        } catch (SQLException e) {

        }
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
                    new ClassPathResource("database/accommodation_amenities/remove-accommodation_amenities.sql")
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
        BookingResponseDto expected = new BookingResponseDto(3L,
                LocalDate.of(2026, 10, 10),
                LocalDate.of(2026, 11, 12),
                1L,
                1L,
                Booking.Status.PENDING

        );
        BookingRequestDto requestDto = new BookingRequestDto()
                .setAccommodationId(1L)
                .setCheckInDate(LocalDate.of(2026, 10, 10))
                .setCheckOutDate(LocalDate.of(2026, 11, 12));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/bookings")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        BookingResponseDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), BookingResponseDto.class);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get bookings by id and status when is provided valid data")
    @WithMockUser(username = "admin@gmail.com", roles = "MANAGER")
    void getBooking_WithValidId_ShouldReturnDto() throws Exception {
        Long user_id = 1L;
        BookingResponseDto expected = getBookingResponseDto();
        List<BookingResponseDto> expectedList = new ArrayList<>();
        expectedList.add(expected);

        MvcResult result = mockMvc.perform(get("/bookings/{user_id}/status", user_id)
                        .param("status", Booking.Status.PENDING.toString())
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();


        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode jsonNode = node.get("content");
        List<BookingResponseDto> actual = objectMapper.readerForListOf(BookingResponseDto.class).readValue(jsonNode);

        assertEquals(expectedList.size(), actual.size());
        assertEquals(expectedList.get(0), actual.get(0));

    }

    @Test
    @DisplayName("Get bookings by current user")
    @WithMockUser(username = "romander@gmail.com", roles = "CUSTOMER")
    void getBookingsByCurrentUser_WithValidUSer_ShouldReturnDto() throws Exception {
        BookingResponseDto expected = getBookingResponseDto();
        List<BookingResponseDto> expectedList = new ArrayList<>();
        expectedList.add(expected);

        MvcResult result = mockMvc.perform(get("/bookings/my")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode jsonNode = node.get("content");
        List<BookingResponseDto> actual = objectMapper.readerForListOf(BookingResponseDto.class).readValue(jsonNode);

        assertEquals(expectedList.size(), actual.size());
        assertEquals(expectedList.get(0), actual.get(0));

    }

    @Test
    @DisplayName("Get specific booking by id when valid id is provided")
    @WithMockUser(username = "admin@gmail.com", roles = "MANAGER")
    void getSpecificBooking_WithValidId_ShouldReturnDto() throws Exception {
        Long id = 1L;
        BookingResponseDto expected = getBookingResponseDto();

        MvcResult result = mockMvc.perform(get("/bookings/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookingResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), BookingResponseDto.class);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("update booking by id when valid id is provided")
    @WithMockUser(username = "admin@gmail.com", roles = "MANAGER")
    void updateBooking_WithValidIDAndRequest_ShouldReturnDto() throws Exception {
        Long id = 2L;
        BookingResponseDto expected = new BookingResponseDto(2L,
                LocalDate.of(2025, 10, 5),
                LocalDate.of(2025, 10, 15),
                1L,
                2L,
                Booking.Status.CONFIRMED);
        BookingRequestDto requestDto = new BookingRequestDto()
                .setAccommodationId(1L)
                .setCheckInDate(LocalDate.of(2025, 10, 5))
                .setCheckOutDate(LocalDate.of(2025, 10, 15));

        MvcResult result = mockMvc.perform(put("/bookings/{id}", id)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookingResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), BookingResponseDto.class);

        assertEquals(expected, actual);
    }


    @Test
    @DisplayName("Update booking status by id and request when valid id and request is provided")
    @WithMockUser(username = "admin@gmail.com", roles = "MANAGER")
    void updateBookingStatus_WithValidRequest_Should() throws Exception {
        Long booking_id = 1L;
        Long user_id = 1L;
        BookingResponseDto expected = new BookingResponseDto(
                1L,
                LocalDate.of(2025, 10, 5),
                LocalDate.of(2025, 10, 15),
                1L,
                1L,
                Booking.Status.CONFIRMED
        );
        BookingUpdateStatusRequestDto requestDto = new BookingUpdateStatusRequestDto()
                .setStatus(Booking.Status.CONFIRMED);

        MvcResult result = mockMvc.perform(put("/bookings/{booking_id}/status", booking_id)
                        .param("user_id", String.valueOf(user_id))
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookingResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), BookingResponseDto.class);

        assertEquals(expected, actual);

    }

    @Test
    @DisplayName("Delete booking by id when valid id is provided")
    @WithMockUser(username = "admin@gmail.com", roles = {"CUSTOMER", "MANAGER"})
    void deleteBooking_WithValidId_ShouldReturnNoContentStatus() throws Exception {
        Long id = 2L;

        mockMvc.perform(delete("/bookings/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/bookings/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }
}
