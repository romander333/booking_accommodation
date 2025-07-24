package com.romander.bookingapp.dto.booking;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class BookingRequestDto {
    @NotNull
    private Long accommodationId;
    @NotNull(message = "Check-in date must not be null")
    private LocalDate checkInDate;
    @NotNull(message = "Check-out date must not be null")
    private LocalDate checkOutDate;
}
