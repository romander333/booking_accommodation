package com.romander.bookingapp.controller;

import com.romander.bookingapp.dto.payment.PaymentRequestDto;
import com.romander.bookingapp.dto.payment.PaymentResponseDto;

import com.romander.bookingapp.exception.EntityNotFoundException;
import com.romander.bookingapp.model.Payment;
import com.romander.bookingapp.repository.PaymentRepository;
import com.romander.bookingapp.service.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    @PostMapping
    public PaymentResponseDto createPayment(@RequestBody @Valid PaymentRequestDto requestDto) throws StripeException {
        return paymentService.pay(requestDto);
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeEvent(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        String endpointSecret = "whsec_25f5d4ed6a48e2b4e163dd81efe59624da062898e2fdab2823bdd73073810c07";
        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event
                    .getDataObjectDeserializer()
                    .getObject()
                    .get();

            String sessionId = session.getId();
            Payment payment = paymentRepository.findBySessionId(sessionId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Payment not found by session id: " + sessionId));
            payment.setStatus(Payment.Status.PAID);
            paymentRepository.save(payment);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Payment created");
    }

    @GetMapping("/success")
    public ResponseEntity<Map<String, Object>> getSuccessPayment() throws StripeException {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Payment successful");
        return ResponseEntity.ok(result);
    }


    @GetMapping("/cancel")
    public ResponseEntity<Map<String, Object>> getCancelPayment() throws StripeException {
        Map<String, Object> result = new HashMap<>();
        result.put("cancel", true);
        result.put("message", "Payment cancelled");
        return ResponseEntity.ok(result);
    }

}
