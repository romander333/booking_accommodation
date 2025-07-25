package com.romander.bookingapp.service;

import com.romander.bookingapp.dto.payment.PaymentRequestDto;
import com.romander.bookingapp.dto.payment.PaymentResponseDto;
import com.stripe.exception.StripeException;

public interface PaymentService {
    PaymentResponseDto pay(PaymentRequestDto paymentRequestDto) throws StripeException;
}
