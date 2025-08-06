package com.romander.bookingapp.service;

import com.romander.bookingapp.dto.booking.BookingRequestDto;
import com.romander.bookingapp.dto.booking.BookingResponseDto;
import com.romander.bookingapp.dto.booking.BookingUpdateStatusRequestDto;
import com.romander.bookingapp.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingService {
    BookingResponseDto createBooking(BookingRequestDto bookingRequestDto);

    Page<BookingResponseDto> getBookingsBuUserIdAndStatus(
            Long userId,
            Booking.Status status,
            Pageable pageable);

    Page<BookingResponseDto> getBookingsByCurrentUser(Pageable pageable);

    BookingResponseDto getBookingById(Long id);

    BookingResponseDto updateBooking(Long id, BookingRequestDto bookingRequestDto);

    void deleteBooking(Long id);

    BookingResponseDto updateBookingStatus(
            Long bookingId,
            Long userId,
            BookingUpdateStatusRequestDto requestDto);
}
