package com.romander.bookingapp.controller;

import com.romander.bookingapp.dto.payment.PaymentResponseDto;

import com.romander.bookingapp.service.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "Payment management", description = "Endpoint for payment management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    @Value("${stripe.webhook.endPoint}")
    private String stripeEndpoint;
    private final PaymentService paymentService;

    @Operation(summary = "Create payment", description = "Create payment fot booking id")
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/{id}")
    public PaymentResponseDto createPayment(@PathVariable Long id) throws StripeException {
        return paymentService.pay(id);
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeEvent(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, stripeEndpoint);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event
                    .getDataObjectDeserializer()
                    .getObject()
                    .orElse(null);

            if (session != null) {
                System.out.println("Payment successful for session: " + session.getId());
                paymentService.handleSuccessfulPayment(session);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body("Payment created");
    }

    @GetMapping("/success")
    public ResponseEntity<Map<String, Object>> getSuccessPayment() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Payment successful");
        paymentService.notifyPaymentSuccess();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/cancel")
    public ResponseEntity<Map<String, Object>> getCancelPayment() {
        Map<String, Object> result = new HashMap<>();
        result.put("cancel", true);
        result.put("message", "Payment cancelled");
        paymentService.notifyPaymentCancelled();
        return ResponseEntity.ok(result);
    }
}
