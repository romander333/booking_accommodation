package com.romander.bookingapp.service;

import com.romander.bookingapp.model.Booking;
import com.romander.bookingapp.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingCleanupService {
    private final BookingRepository bookingRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 0 * * *")
    public void cancelStaleBookings() {
        LocalDateTime timeLimit = LocalDateTime.now().minusHours(24);
        List<Booking> outdated = bookingRepository.findPendingBookingsCreatedBefore(timeLimit);
        if (outdated.isEmpty()) {
            notificationService.sendMessage("No expired bookings today!");
        }
        for (Booking booking : outdated) {
            booking.setStatus(Booking.Status.EXPIRED);
            notificationService.sendMessage(String.format("""
                            Accommodation expired
                                            🧑 User: %s
                                            🏠 Accommodation: %s
                                            📅 From: %s
                                            📅 To: %s
                                            💼 Booking ID: %s
                            """,
                    booking.getUser().getEmail(),
                    booking.getAccommodation().getId(),
                    booking.getCheckInDate(),
                    booking.getCheckOutDate(),
                    booking.getId()
            ));
        }
        bookingRepository.saveAll(outdated);
    }
}
