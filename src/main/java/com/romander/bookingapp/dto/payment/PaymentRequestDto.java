package com.romander.bookingapp.dto.payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequestDto {
    @NotNull
    private Long bookingId;
    @DecimalMin(value = "1.0", inclusive = false)
    private BigDecimal amount;
}
