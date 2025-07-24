package com.romander.bookingapp.controller;

import com.romander.bookingapp.dto.booking.BookingRequestDto;
import com.romander.bookingapp.dto.booking.BookingResponseDto;
import com.romander.bookingapp.dto.booking.BookingUpdateStatusRequestDto;
import com.romander.bookingapp.mapper.BookingMapper;
import com.romander.bookingapp.model.Booking;
import com.romander.bookingapp.service.BookingService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto createBooking(@RequestBody @Valid BookingRequestDto bookingRequestDto) {
        return bookingService.createBooking(bookingRequestDto);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/{user_id}/status")
    public Page<BookingResponseDto> getBooking(@PathVariable Long user_id, Booking.Status status, Pageable pageable) {
        return bookingService.getBookingsBuUserIdAndStatus(user_id, status, pageable);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public Page<BookingResponseDto> getBookingsByCurrentUser(Pageable pageable) {
        return bookingService.getBookingsByCurrentUser(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public BookingResponseDto getSpecificBooking(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public BookingResponseDto updateBooking(@PathVariable Long id, @RequestBody @Valid BookingRequestDto requestDto) {
        return bookingService.updateBooking(id, requestDto);
    }

    @PutMapping("/{booking_id}/status")
    @PreAuthorize("hasRole('MANAGER')")
    public BookingResponseDto updateBookingStatus(@PathVariable Long booking_id, @RequestParam Long user_id, @RequestBody @Valid BookingUpdateStatusRequestDto requestDto) {
        return bookingService.updateBookingStatus(booking_id, user_id, requestDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
    }

}
