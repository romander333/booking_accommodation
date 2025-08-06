package com.romander.bookingapp.dto.booking;

import com.romander.bookingapp.model.Booking;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BookingResponseDto {
    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Long accommodationId;
    private Long userId;
    private LocalDateTime createdAt;
    private Booking.Status status;
}
