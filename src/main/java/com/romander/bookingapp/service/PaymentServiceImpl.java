package com.romander.bookingapp.service;

import com.romander.bookingapp.dto.payment.PaymentRequestDto;
import com.romander.bookingapp.dto.payment.PaymentResponseDto;
import com.romander.bookingapp.mapper.PaymentMapper;
import com.romander.bookingapp.model.Payment;
import com.romander.bookingapp.repository.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public PaymentResponseDto pay(PaymentRequestDto requestDto) throws StripeException {
        Payment payment = paymentMapper.toModel(requestDto);
        payment.setStatus(Payment.Status.PENDING);
        SessionCreateParams params = new SessionCreateParams.Builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/api/payments/success")
                .setCancelUrl("http://localhost:8080/api/payments/cancel")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(convernToLong(requestDto.getAmount()))
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Test Product")
                                                                .build()
                                                )
                                                .build()
                                )
                                .setQuantity(1L)
                                .build()
                )
                .build();

        Session session = Session.create(params);
        payment.setSessionUrl(session.getUrl());
        payment.setSessionId(session.getId());
        paymentRepository.save(payment);
        return paymentMapper.toDto(payment);
    }

    private Long convernToLong(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(100)).longValue();
    }
}
