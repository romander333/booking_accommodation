package com.romander.bookingapp.service;

import static com.romander.bookingapp.util.AccommodationDataTest.getAccommodation;
import static com.romander.bookingapp.util.BookingDataTest.getAnotherBooking;
import static com.romander.bookingapp.util.BookingDataTest.getAnotherBookingResponseDto;
import static com.romander.bookingapp.util.BookingDataTest.getBooking;
import static com.romander.bookingapp.util.BookingDataTest.getBookingRequestDto;
import static com.romander.bookingapp.util.BookingDataTest.getBookingResponseDto;
import static com.romander.bookingapp.util.BookingDataTest.getBookingResponseDtoFotUpdateAndCreate;
import static com.romander.bookingapp.util.UserDataTest.sampleUser;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.romander.bookingapp.dto.booking.BookingRequestDto;
import com.romander.bookingapp.dto.booking.BookingResponseDto;
import com.romander.bookingapp.dto.booking.BookingUpdateStatusRequestDto;
import com.romander.bookingapp.mapper.BookingMapper;
import com.romander.bookingapp.model.Accommodation;
import com.romander.bookingapp.model.Booking;
import com.romander.bookingapp.model.User;
import com.romander.bookingapp.repository.AccommodationRepository;
import com.romander.bookingapp.repository.BookingRepository;
import com.romander.bookingapp.security.AuthenticationService;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("Create Booking when valid request is provided")
    void createBooking_WithValidRequest_ShouldReturnDto() {
        Long id = 1L;
        Accommodation accommodation = getAccommodation();
        User user = sampleUser(id);
        Booking booking = getBooking();
        BookingRequestDto requestDto = getBookingRequestDto();
        BookingResponseDto expected = getBookingResponseDto();
        String message = "";

        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(expected);
        when(bookingMapper.toModel(requestDto)).thenReturn(booking);
        when(accommodationRepository.findById(id)).thenReturn(Optional.of(accommodation));
        when(authenticationService.getCurrentUser()).thenReturn(user);

        BookingResponseDto actual = bookingService.createBooking(requestDto);

        assertEquals(expected, actual);
        verify(bookingRepository).save(booking);
        verify(authenticationService).getCurrentUser();
        verify(accommodationRepository).findById(id);
    }

    @Test
    @DisplayName("Get Bookings by user id and status when valid data is provided")
    void getBookingsBuUserIdAndStatus_WithValidUserIdAndStatus_ShouldReturnPage() {
        Long id = 2L;
        Pageable pageable = PageRequest.of(0, 10);
        Booking.Status status = Booking.Status.CONFIRMED;
        Booking booking = getAnotherBooking();
        BookingResponseDto expected = getAnotherBookingResponseDto();
        Page<Booking> expectedPage = new PageImpl<>(Arrays.asList(booking));

        when(bookingMapper.toDto(booking)).thenReturn(expected);
        when(bookingRepository
                .findAllByUser_IdAndStatus(id, status, pageable)).thenReturn(expectedPage);

        Page<BookingResponseDto> actual =
                bookingService.getBookingsBuUserIdAndStatus(id, status, pageable);

        assertEquals(expectedPage.getSize(), actual.getTotalElements());
        assertEquals(expected, actual.getContent().get(0));

        verify(bookingRepository).findAllByUser_IdAndStatus(id, status, pageable);
    }

    @Test
    @DisplayName("Get Bookings by current user when user is authenticated")
    void getBookingsByCurrentUser_WithCurrentUser_ShouldReturnPage() {
        Long id = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        User user = sampleUser(id);
        Booking booking = getBooking();
        BookingResponseDto expected = getBookingResponseDto();
        Page<Booking> expectedPage = new PageImpl<>(Arrays.asList(booking));

        when(bookingMapper.toDto(booking)).thenReturn(expected);
        when(bookingRepository.findBookingByUser_Id(id, pageable)).thenReturn(expectedPage);
        when(authenticationService.getCurrentUser()).thenReturn(user);

        Page<BookingResponseDto> actual = bookingService.getBookingsByCurrentUser(pageable);

        assertEquals(expectedPage.getSize(), actual.getTotalElements());
        assertEquals(expected, actual.getContent().get(0));
    }

    @Test
    @DisplayName("Get Booking by id when valid booking id is provided")
    void getBookingById_WithValidId_ShouldReturnDto() {
        Long id = 2L;
        Booking booking = getAnotherBooking();
        BookingResponseDto expected = getAnotherBookingResponseDto();

        when(bookingRepository.findById(id)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(expected);

        BookingResponseDto actual = bookingService.getBookingById(id);

        assertEquals(expected, actual);
        verify(bookingRepository).findById(id);
    }

    @Test
    @DisplayName("Update Booking by id when valid booking id is provided")
    void updateBooking_WithValidId_ShouldUpdateAndReturnDto() {
        Long id = 2L;
        Accommodation accommodation = getAccommodation();
        Booking booking = getAnotherBooking();
        BookingResponseDto expected = getBookingResponseDto();
        BookingRequestDto requestDto = getBookingRequestDto();

        when(bookingRepository.findById(id)).thenReturn(Optional.of(booking));
        doNothing().when(bookingMapper).updateBooking(booking, requestDto);
        when(bookingMapper.toDto(booking)).thenReturn(expected);
        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodation));

        BookingResponseDto actual = bookingService.updateBooking(id, requestDto);

        assertEquals(expected, actual);
        verify(bookingRepository).findById(id);
    }

    @Test
    @DisplayName("Delete Booking by id when valid booking id is provided")
    void deleteBooking_WithValidId_ShouldReturnDto() {
        Long id = 2L;
        Booking booking = getAnotherBooking();
        Booking savedBooking = getAnotherBooking();
        savedBooking.setStatus(Booking.Status.CANCELED);

        when(bookingRepository.findById(id)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(savedBooking);
        when(authenticationService.getCurrentUser()).thenReturn(sampleUser(id));

        bookingService.deleteBooking(id);
        verify(bookingRepository).findById(id);
        verify(bookingRepository).save(savedBooking);
    }

    @Test
    @DisplayName("Update Booking status by user id, booking id when valid ids is provided")
    void updateBookingStatus_WithValidIdAndRequest_ShouldReturnDto() {
        Booking savedBooking = getAnotherBooking();
        savedBooking.setStatus(Booking.Status.EXPIRED);
        BookingUpdateStatusRequestDto requestDto = new BookingUpdateStatusRequestDto();
        requestDto.setStatus(Booking.Status.EXPIRED);
        BookingResponseDto expected = getBookingResponseDtoFotUpdateAndCreate();
        expected.setStatus(Booking.Status.EXPIRED);
        Long id = 2L;
        Booking booking = getAnotherBooking();

        when(bookingRepository.findBookingByUser_IdAndId(id, id)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(expected);
        when(bookingRepository.save(booking)).thenReturn(savedBooking);

        BookingResponseDto actual = bookingService.updateBookingStatus(id, id, requestDto);
        assertEquals(expected, actual);
        verify(bookingRepository).findBookingByUser_IdAndId(id, id);
        verify(bookingRepository).save(savedBooking);
    }
}
