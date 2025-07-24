package com.romander.bookingapp.util;

import com.romander.bookingapp.dto.booking.BookingRequestDto;
import com.romander.bookingapp.dto.booking.BookingResponseDto;
import com.romander.bookingapp.model.Booking;
import com.romander.bookingapp.model.User;

import java.time.LocalDate;

import static com.romander.bookingapp.util.AccommodationDataTest.getAccommodation;
import static com.romander.bookingapp.util.UserDataTest.sampleUser;

public class BookingDataTest {

    public static Booking getBooking() {
        Booking booking = new Booking();
        User user = sampleUser(1L);
        booking.setUser(user);
        booking.setAccommodation(getAccommodation());
        booking.setId(1L);
        booking.setCheckInDate(LocalDate.of(2025, 10, 5));
        booking.setCheckOutDate(LocalDate.of(2025, 10, 15));
        booking.setStatus(Booking.Status.PENDING);
        return booking;
    }

    public static Booking getAnotherBooking() {
        Booking booking = new Booking();
        User user = sampleUser(2L);
        booking.setUser(user);
        booking.setAccommodation(getAccommodation());
        booking.setId(2L);
        booking.setCheckInDate(LocalDate.of(2025, 8, 5));
        booking.setCheckOutDate(LocalDate.of(2025, 8, 20));
        booking.setStatus(Booking.Status.CONFIRMED);
        return booking;
    }

    public static BookingResponseDto getBookingResponseDto() {
        return new BookingResponseDto(
                1L,
                LocalDate.of(2025, 10, 5),
                LocalDate.of(2025, 10, 15),
                1L,
                1L,
                Booking.Status.PENDING
        );
    }
    public static BookingResponseDto getAnotherBookingResponseDto() {
        return new BookingResponseDto(
                2L,
                LocalDate.of(2025, 8, 5),
                LocalDate.of(2025, 8, 20),
                2L,
                2L,
                Booking.Status.CONFIRMED
        );
    }

    public static BookingRequestDto getBookingRequestDto() {
        return new BookingRequestDto()
                .setAccommodationId(1L)
                .setCheckInDate(LocalDate.of(2025, 10, 5))
                .setCheckOutDate(LocalDate.of(2025, 10, 15));
    }
}
