package com.romander.bookingapp.controller;

import static com.romander.bookingapp.model.Accommodation.Type.CONDO;
import static com.romander.bookingapp.util.AccommodationDataTest.getAccommodationRequestDto;
import static com.romander.bookingapp.util.AccommodationDataTest.getAccommodationResponseDto;
import static com.romander.bookingapp.util.AccommodationDataTest.getAnotherAccommodationResponseDto;
import static com.romander.bookingapp.util.AddressDataTest.getAddress;
import static com.romander.bookingapp.util.AddressDataTest.getAddressDto;
import static com.romander.bookingapp.util.AmenitiesDataTest.getAmenities;
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
import com.romander.bookingapp.dto.accommodation.AccommodationRequestDto;
import com.romander.bookingapp.dto.accommodation.AccommodationResponseDto;
import com.romander.bookingapp.dto.accommodation.AddressDto;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import com.romander.bookingapp.service.NotificationService;
import com.romander.bookingapp.service.PaymentService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
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
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccommodationControllerTest {
    private static MockMvc mockMvc;

    @Mock
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    void beforeAll(@Autowired WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void setUp(@Autowired DataSource dataSource) {
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/accommodation/add-accommodation.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/accommodation_amenities/add-accommodation_amenities.sql")
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
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
    @WithMockUser(username = "manager", roles = "MANAGER")
    @DisplayName("Create Accommodation when valid request provided")
    void createAccommodation_WithValidData_ShouldReturnAccommodation() throws Exception {
        AddressDto addressDto = getAddressDto();
        AccommodationResponseDto expected = new AccommodationResponseDto()
                .setId(3L)
                .setType(CONDO)
                .setLocation(addressDto)
                .setDailyRate(BigDecimal.valueOf(3500))
                .setAmenities(getAmenities())
                .setSize("Big room")
                .setAvailability(4);
        AccommodationRequestDto requestDto = new AccommodationRequestDto()
                .setType(expected.getType())
                .setAvailability(expected.getAvailability())
                .setLocation(getAddress())
                .setDailyRate(expected.getDailyRate())
                .setAmenities(getAmenities())
                .setSize(expected.getSize());

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(post("/accommodations")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        AccommodationResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), AccommodationResponseDto.class);

        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "customer", roles = "CUSTOMER")
    @DisplayName("Get all Accommodation when valid catalog is provided")
    void getAccommodations_WithGivenCatalog_ShouldReturnPage() throws Exception {
        List<AccommodationResponseDto> expected = new ArrayList<>();
        expected.add(getAccommodationResponseDto());
        expected.add(getAnotherAccommodationResponseDto());

        MvcResult result = mockMvc.perform(get("/accommodations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode node = jsonNode.get("content");
        AccommodationResponseDto[] actual = objectMapper.treeToValue(
                node, AccommodationResponseDto[].class);
        for (int i = 0; i < actual.length - 1; i++) {
            expected.get(i).setDailyRate(expected.get(i).getDailyRate().setScale(1));
            assertEquals(expected.get(i), actual[i]);
        }
    }

    @Test
    @WithMockUser(username = "customer", roles = "CUSTOMER")
    @DisplayName("Get Accommodation when valid id is provided")
    void getAccommodationById_WithValidId_ShouldReturnAccommodationResponse() throws Exception {
        Long id = 2L;
        AccommodationResponseDto expected = getAnotherAccommodationResponseDto();

        MvcResult result = mockMvc.perform(get("/accommodations/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        AccommodationResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), AccommodationResponseDto.class);
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "manager", roles = "MANAGER")
    @DisplayName("Update Accommodation when valid id is provided")
    void updateAccommodation_WithValidIdAndRequest_ShouldUpdateAccommodation() throws Exception {
        Long id = 2L;
        AccommodationResponseDto expected = getAccommodationResponseDto();
        AccommodationRequestDto requestDto = getAccommodationRequestDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(put("/accommodations/{id}", id)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        AccommodationResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), AccommodationResponseDto.class);

        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER", "CUSTOMER"})
    @DisplayName("Delete Accommodation when invalid id is provided")
    void deleteAccommodation_WithValidId_ShouldDeleteData() throws Exception {
        Long id = 2L;

        mockMvc.perform(delete("/accommodations/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        mockMvc.perform(get("/accommodations/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
