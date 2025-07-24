package com.romander.bookingapp.repository;

import com.romander.bookingapp.dto.booking.BookingResponseDto;
import com.romander.bookingapp.model.Booking;
import com.romander.bookingapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByUser_IdAndStatus(Long userId, Booking.Status status, Pageable pageable);

    Page<Booking> findBookingByUser_Id(Long userId, Pageable pageable);

    Optional<Booking> findBookingByUser_IdAndId(Long userId, Long id);

}
