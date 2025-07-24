package com.romander.bookingapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.net.URL;

@Entity
@Getter
@Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Status status;
    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
    private URL sessionUrl;
    private String sessionId;
    private BigDecimal amount;


    public enum Status {
        PENDING, EXPIRED, PAID
    }
}
