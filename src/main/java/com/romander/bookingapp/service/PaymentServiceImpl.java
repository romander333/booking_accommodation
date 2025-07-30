package com.romander.bookingapp.service;

import com.romander.bookingapp.dto.payment.PaymentResponseDto;
import com.romander.bookingapp.exception.EntityNotFoundException;
import com.romander.bookingapp.mapper.PaymentMapper;
import com.romander.bookingapp.model.Booking;
import com.romander.bookingapp.model.Payment;
import com.romander.bookingapp.repository.BookingRepository;
import com.romander.bookingapp.repository.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final NotificationService notificationService;

    @Override
    public PaymentResponseDto pay(Long id) throws StripeException {
        Booking booking = getBooking(id);
        Long amount = calculateTotalPrice(booking);
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
                                                .setUnitAmount(amount)
                                                .setProductData(
                                                        SessionCreateParams.LineItem
                                                                .PriceData
                                                                .ProductData.builder()
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
        Payment payment = buildPayment(booking, session, amount);
        paymentRepository.save(payment);
        return paymentMapper.toDto(payment);
    }

    @Override
    public void handleSuccessfulPayment(Session session) {
        String sessionId = session.getId();

        Payment payment = paymentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Payment not found by session id: " + sessionId));

        payment.setStatus(Payment.Status.PAID);
        paymentRepository.save(payment);
    }

    public void notifyPaymentSuccess() {
        notificationService.sendMessage("A customer has successfully completed a payment.");
    }

    public void notifyPaymentCancelled() {
        notificationService.sendMessage(String.format("""
            Payment was cancelled
            Time: %s
                """, LocalDateTime.now()));
    }

    private Booking getBooking(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found by id: " + id));
    }

    private Long calculateTotalPrice(Booking booking) {
        long totalDays = ChronoUnit.DAYS.between(booking.getCheckInDate(),
                booking.getCheckOutDate());
        long price = booking
                .getAccommodation()
                .getDailyRate()
                .multiply(BigDecimal.valueOf(100)).longValue();
        return price * totalDays;
    }

    private Payment buildPayment(Booking booking, Session session, Long amount) {
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(BigDecimal.valueOf(amount));
        payment.setStatus(Payment.Status.PENDING);
        payment.setSessionId(session.getId());
        payment.setSessionUrl(session.getUrl());
        return payment;
    }
}
