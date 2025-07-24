package com.romander.bookingapp.service;

import com.romander.bookingapp.dto.booking.BookingRequestDto;
import com.romander.bookingapp.dto.booking.BookingResponseDto;
import com.romander.bookingapp.dto.booking.BookingUpdateStatusRequestDto;
import com.romander.bookingapp.exception.EntityNotFoundException;
import com.romander.bookingapp.mapper.BookingMapper;
import com.romander.bookingapp.model.Accommodation;
import com.romander.bookingapp.model.Booking;
import com.romander.bookingapp.model.User;
import com.romander.bookingapp.repository.AccommodationRepository;
import com.romander.bookingapp.repository.BookingRepository;
import com.romander.bookingapp.repository.UserRepository;
import com.romander.bookingapp.security.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final AuthenticationService authenticationService;
    private final AccommodationRepository accommodationRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto) {
        User currentUser = getCurrentUser();
        Accommodation accommodation = accommodationRepository.findById(bookingRequestDto.getAccommodationId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Accommodation not found by id: " + bookingRequestDto.getAccommodationId()));
        Booking booking = bookingMapper.toModel(bookingRequestDto);
        booking.setAccommodation(accommodation);
        booking.setStatus(Booking.Status.PENDING);
        booking.setUser(currentUser);
        Booking save = bookingRepository.save(booking);
        return bookingMapper.toDto(save);
    }

    @Override
    public Page<BookingResponseDto> getBookingsBuUserIdAndStatus(
            Long userId,
            Booking.Status status,
            Pageable pageable) {
        return bookingRepository.findAllByUser_IdAndStatus(userId, status, pageable)
                .map(bookingMapper::toDto);
    }

    @Override
    public Page<BookingResponseDto> getBookingsByCurrentUser(Pageable pageable) {
        User currentUser = getCurrentUser();
        return bookingRepository.findBookingByUser_Id(currentUser.getId(), pageable)
                .map(bookingMapper::toDto);
    }

    @Override
    public BookingResponseDto getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't found booking by id: " + id));
        return bookingMapper.toDto(booking);
    }

    @Override
    @Transactional
    public BookingResponseDto updateBooking(Long id, BookingRequestDto bookingRequestDto) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't found booking by id: " + id));
        Accommodation accommodation = accommodationRepository.findById(bookingRequestDto.getAccommodationId())
                        .orElseThrow(() -> new EntityNotFoundException("Can't found accommodation by id: " + id));
        booking.setAccommodation(accommodation);
        bookingMapper.updateBooking(booking, bookingRequestDto);

        return bookingMapper.toDto(booking);
    }

    @Override
    public void deleteBooking(Long id) {
        bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't found booking by id: " + id));
        bookingRepository.deleteById(id);
    }

    @Override
    @Transactional
    public BookingResponseDto updateBookingStatus(Long booking_id, Long user_id, BookingUpdateStatusRequestDto requestDto) {
        Booking booking = bookingRepository.findBookingByUser_IdAndId(user_id, booking_id)
                        .orElseThrow(() -> new EntityNotFoundException("Can't found booking by id: " + booking_id + "or user id: " + user_id));
        booking.setStatus(requestDto.getStatus());
        Booking saveBooking = bookingRepository.save(booking);

        return bookingMapper.toDto(saveBooking);
    }

    private User getCurrentUser() {
        return authenticationService.getCurrentUser();
    }

}
