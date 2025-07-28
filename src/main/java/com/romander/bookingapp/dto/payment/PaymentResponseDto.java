package com.romander.bookingapp.dto.payment;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PaymentResponseDto {
    private String sessionId;
    private String sessionUrl;
}
