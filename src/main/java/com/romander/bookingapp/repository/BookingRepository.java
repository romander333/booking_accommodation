package com.romander.bookingapp.repository;

import com.romander.bookingapp.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByUser_IdAndStatus(Long userId, Booking.Status status, Pageable pageable);

    Page<Booking> findBookingByUser_Id(Long userId, Pageable pageable);

    Optional<Booking> findBookingByUser_IdAndId(Long userId, Long id);

    @Query("SELECT b FROM Booking b "
            + "WHERE b.accommodation.id = :accommodationId "
            + "AND b.status NOT IN ('CANCELED', 'EXPIRED')"
            + "AND b.checkInDate < :checkOutDate "
            + "AND b.checkOutDate > :checkInDate")
    List<Booking> findOverlappingBookings(Long accommodationId,
                                          LocalDate checkInDate,
                                          LocalDate checkOutDate);

    @Query("SELECT b FROM Booking b WHERE b.status = 'PENDING' AND b.createdAt <= :limitTime")
    List<Booking> findPendingBookingsCreatedBefore(@Param("limitTime") LocalDateTime limitTime);

    @EntityGraph(attributePaths = {"accommodation.dailyRate"})
    Optional<Booking> findById(Long id);
}
