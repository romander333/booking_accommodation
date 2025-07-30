package com.romander.bookingapp.util;

import static com.romander.bookingapp.util.AccommodationDataTest.getAccommodation;
import static com.romander.bookingapp.util.UserDataTest.sampleUser;

import com.romander.bookingapp.dto.booking.BookingRequestDto;
import com.romander.bookingapp.dto.booking.BookingResponseDto;
import com.romander.bookingapp.model.Booking;
import com.romander.bookingapp.model.User;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingDataTest {

    public static Booking getBooking() {
        Booking booking = new Booking();
        User user = sampleUser(1L);
        booking.setUser(user);
        booking.setAccommodation(getAccommodation());
        booking.setId(1L);
        booking.setCheckInDate(LocalDate.of(2025, 10, 5));
        booking.setCheckOutDate(LocalDate.of(2025, 10, 15));
        booking.setCreatedAt(LocalDateTime.of(2025, 7, 30, 10,0,0));
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
        return new BookingResponseDto()
                .setId(1L)
                .setCheckInDate(LocalDate.of(2025, 10, 5))
                .setCheckOutDate(LocalDate.of(2025, 10, 15))
                .setAccommodationId(1L)
                .setUserId(1L)
                .setCreatedAt(LocalDateTime.of(2025, 7, 30, 10,0,0))
                .setStatus(Booking.Status.PENDING);
    }

    public static BookingResponseDto getAnotherBookingResponseDto() {
        return new BookingResponseDto()
                .setId(2L)
                .setCheckInDate(LocalDate.of(2025, 8, 15))
                .setCheckOutDate(LocalDate.of(2025, 8, 20))
                .setAccommodationId(2L)
                .setUserId(1L)
                .setCreatedAt(LocalDateTime.of(2025, 7, 30, 10,0,0))
                .setStatus(Booking.Status.CONFIRMED);
    }

    public static BookingResponseDto getBookingResponseDtoForUpdateAndCreate() {
        return new BookingResponseDto()
                .setId(3L)
                .setCheckInDate(LocalDate.of(2026, 10, 10))
                .setCheckOutDate(LocalDate.of(2026, 11, 12))
                .setAccommodationId(1L)
                .setUserId(1L)
                .setCreatedAt(LocalDateTime.of(2025, 7, 30, 10,0,0))
                .setStatus(Booking.Status.PENDING);
    }

    public static BookingResponseDto getBookingResponseDtoFotUpdateAndCreate() {
        return new BookingResponseDto()
                .setId(3L)
                .setCheckInDate(LocalDate.of(2025, 10, 5))
                .setCheckOutDate(LocalDate.of(2025, 10, 15))
                .setAccommodationId(1L)
                .setUserId(1L)
                .setCreatedAt(LocalDateTime.of(2025, 7, 30, 10,0,0))
                .setStatus(Booking.Status.PENDING);
    }

    public static BookingRequestDto getBookingRequestDto() {
        return new BookingRequestDto()
                .setAccommodationId(1L)
                .setCheckInDate(LocalDate.of(2025, 10, 5))
                .setCheckOutDate(LocalDate.of(2025, 10, 15));
    }

    public static BookingRequestDto getAnotherBookingRequestDto() {
        return new BookingRequestDto()
                .setAccommodationId(1L)
                .setCheckInDate(LocalDate.of(2026, 10, 10))
                .setCheckOutDate(LocalDate.of(2026, 11, 12));
    }
}
