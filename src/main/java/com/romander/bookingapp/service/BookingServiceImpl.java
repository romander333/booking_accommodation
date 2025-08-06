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
import com.romander.bookingapp.security.AuthenticationService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final NotificationService notificationService;
    private final AuthenticationService authenticationService;
    private final AccommodationRepository accommodationRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto) {
        User currentUser = getCurrentUser();
        Accommodation accommodation = validateAccommodationExists(bookingRequestDto);
        checkAccommodationAvailability(bookingRequestDto, accommodation);
        Booking savedBooking = buildBooking(bookingRequestDto, accommodation, currentUser);
        sendNewBookingNotification(
                bookingRequestDto,
                accommodation,
                currentUser,
                savedBooking);
        return bookingMapper.toDto(savedBooking);
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
        Booking booking = getBooking(id);
        return bookingMapper.toDto(booking);
    }

    @Override
    @Transactional
    public BookingResponseDto updateBooking(Long id, BookingRequestDto bookingRequestDto) {
        Booking booking = getBooking(id);
        Accommodation accommodation = validateAccommodationExists(bookingRequestDto);
        booking.setAccommodation(accommodation);
        bookingMapper.updateBooking(booking, bookingRequestDto);
        return bookingMapper.toDto(booking);
    }

    @Override
    public void deleteBooking(Long id) {
        Booking booking = getBooking(id);
        if (booking.getStatus() == Booking.Status.CANCELED) {
            throw new IllegalStateException("This accommodation by booking id: "
                    + id
                    + " has already been canceled");
        }
        booking.setStatus(Booking.Status.CANCELED);
        bookingRepository.save(booking);
        notificationService.sendMessage(String.format(
                "⚠️ Booking ID %d was cancelled/deleted by user %s",
                booking.getId(),
                getCurrentUser().getEmail()
        ));
    }

    @Override
    @Transactional
    public BookingResponseDto updateBookingStatus(
            Long bookingId,
            Long userId,
            BookingUpdateStatusRequestDto requestDto) {
        Booking booking = bookingRepository.findBookingByUser_IdAndId(userId, bookingId)
                        .orElseThrow(() ->
                                new EntityNotFoundException("Can't found booking by id: "
                                + bookingId
                                + " or user id: "
                                + userId));
        booking.setStatus(requestDto.getStatus());
        Booking saveBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(saveBooking);
    }

    private User getCurrentUser() {
        return authenticationService.getCurrentUser();
    }

    private void checkAccommodationAvailability(
            BookingRequestDto requestDto,
            Accommodation accommodation) {
        if (accommodation.getAvailability() <= bookingRepository
                .findOverlappingBookings(
                        accommodation.getId(),
                        requestDto.getCheckInDate(),
                        requestDto.getCheckOutDate()).size()) {
            throw new EntityNotFoundException("This accommodation by id: "
                    + accommodation.getId()
                    + " has already not available.");
        }
    }

    private void sendNewBookingNotification(
            BookingRequestDto requestDto,
            Accommodation accommodation,
            User currentUser,
            Booking savedBooking) {
        notificationService.sendMessage(String.format("""
                NEW BOOKING CREATED
                            🧑 User: %s
                            🏠 Accommodation: %s
                            📅 From: %s
                            📅 To: %s
                            💼 Booking ID: %s
                            Status: PENDING
                """,
                currentUser.getEmail(),
                accommodation.getId(),
                savedBooking.getCheckInDate(),
                savedBooking.getCheckOutDate(),
                savedBooking.getId()
        ));
    }

    private Booking buildBooking(
            BookingRequestDto bookingRequestDto,
            Accommodation accommodation,
            User currentUser) {
        Booking booking = bookingMapper.toModel(bookingRequestDto);
        booking.setAccommodation(accommodation);
        booking.setStatus(Booking.Status.PENDING);
        booking.setUser(currentUser);
        booking.setCreatedAt(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    private Accommodation validateAccommodationExists(BookingRequestDto bookingRequestDto) {
        return accommodationRepository
                .findById(bookingRequestDto.getAccommodationId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Accommodation not found by id: "
                                + bookingRequestDto.getAccommodationId()));
    }

    private Booking getBooking(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't found booking by id: " + id));
    }
}
