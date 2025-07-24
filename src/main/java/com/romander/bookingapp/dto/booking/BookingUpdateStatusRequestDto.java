package com.romander.bookingapp.dto.booking;

import com.romander.bookingapp.model.Booking;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BookingUpdateStatusRequestDto {
    @NotNull
    private Booking.Status status;
}
