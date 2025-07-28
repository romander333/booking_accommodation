package com.romander.bookingapp.dto.booking;

import com.romander.bookingapp.model.Booking;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookingResponseDto(
        Long id,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Long accommodationId,
        Long userId,
        LocalDateTime createdAt,
        Booking.Status status) {
}
