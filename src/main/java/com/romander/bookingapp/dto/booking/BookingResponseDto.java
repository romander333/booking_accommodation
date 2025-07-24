package com.romander.bookingapp.dto.booking;

import com.romander.bookingapp.model.Booking;

import java.time.LocalDate;

public record BookingResponseDto(
        Long id,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Long accommodationId,
        Long userId,
        Booking.Status status) {
}
