package com.romander.bookingapp.repository;

import static com.romander.bookingapp.util.BookingDataTest.getBooking;
import static com.romander.bookingapp.util.UserDataTest.getManagerSampleUser;
import static org.junit.Assert.assertEquals;

import com.romander.bookingapp.model.Booking;
import com.romander.bookingapp.model.User;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @BeforeAll
    static void beforeAll(@Autowired DataSource dataSource) {
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
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/booking/add-booking.sql")
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Find all Booking by valid id and status")
    void findAllByUser_IdAndStatus_WithValidId_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Booking expected = getBooking();
        Long userId = 2L;
        Booking.Status status = Booking.Status.PENDING;
        Page<Booking> actual = bookingRepository
                .findAllByUser_IdAndStatus(userId, status, pageable);
        assertEquals(expected, actual.getContent().get(0));
    }

    @Test
    @DisplayName("Find booking by valid user")
    void findBookingByUser_WithValidUser_ShouldReturn_Page() {
        User user = getManagerSampleUser();
        Booking expected = getBooking();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> actual = bookingRepository.findBookingByUser_Id(user.getId(), pageable);
        assertEquals(expected, actual.getContent().get(0));
    }

    @Test
    @DisplayName("Find booking by valid user id and booking id")
    void findBookingByUser_IdAndId_WithValidId_ShouldReturnBooking() {
        Booking expected = getBooking();
        Long userAndAccommodationId = 2L;
        Optional<Booking> actual = bookingRepository
                .findBookingByUser_IdAndId(userAndAccommodationId, 1L);
        assertEquals(expected, actual.get());
    }
}
