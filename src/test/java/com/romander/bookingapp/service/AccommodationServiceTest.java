package com.romander.bookingapp.service;

import static com.romander.bookingapp.util.AccommodationDataTest.getAccommodation;
import static com.romander.bookingapp.util.AccommodationDataTest.getAccommodationRequestDto;
import static com.romander.bookingapp.util.AccommodationDataTest.getAccommodationResponseDto;
import static com.romander.bookingapp.util.AccommodationDataTest.getAnotherAccommodation;
import static com.romander.bookingapp.util.AccommodationDataTest.getAnotherAccommodationResponseDto;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.romander.bookingapp.dto.accommodation.AccommodationRequestDto;
import com.romander.bookingapp.dto.accommodation.AccommodationResponseDto;
import com.romander.bookingapp.exception.EntityNotFoundException;
import com.romander.bookingapp.mapper.AccommodationMapper;
import com.romander.bookingapp.model.Accommodation;
import com.romander.bookingapp.repository.AccommodationRepository;
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
public class AccommodationServiceTest {
    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AccommodationServiceImpl accommodationService;

    @Mock
    private AccommodationMapper accommodationMapper;

    @Test
    @DisplayName("Create Accommodation when valid request provided")
    void createAccommodation_WithValidData_ShouldReturnAccommodation() {
        Accommodation accommodation = getAccommodation();
        AccommodationResponseDto expected = getAccommodationResponseDto();
        AccommodationRequestDto requestDto = getAccommodationRequestDto();

        when(accommodationMapper.toModel(requestDto)).thenReturn(accommodation);
        when(accommodationRepository.save(accommodation)).thenReturn(accommodation);
        when(accommodationMapper.toDto(accommodation)).thenReturn(expected);

        AccommodationResponseDto actual = accommodationService.createAccommodation(requestDto);

        assertEquals(expected, actual);
        verify(accommodationRepository).save(accommodation);
    }

    @Test
    @DisplayName("Get all Accommodation when valid catalog provided")
    void getAccommodations_WithGivenCatalog_ShouldReturnPage() {
        Accommodation accommodation = getAccommodation();
        AccommodationResponseDto expected1 = getAccommodationResponseDto();
        AccommodationResponseDto expected2 = getAnotherAccommodationResponseDto();
        Accommodation accommodation2 = getAnotherAccommodation();

        Page<Accommodation> responseDtoPage =
                new PageImpl<>(Arrays.asList(accommodation, accommodation2));
        Pageable pageable = PageRequest.of(0, 10);
        when(accommodationRepository.findAllWithAmenities(pageable)).thenReturn(responseDtoPage);
        when(accommodationMapper.toDto(accommodation)).thenReturn(expected1);
        when(accommodationMapper.toDto(accommodation2)).thenReturn(expected2);

        Page<AccommodationResponseDto> actual = accommodationService.getAccommodations(pageable);
        assertEquals(expected1, actual.getContent().get(0));
        assertEquals(expected2, actual.getContent().get(1));

        verify(accommodationRepository).findAllWithAmenities(pageable);
    }

    @Test
    @DisplayName("Get Accommodation when valid id provided")
    void getAccommodationById_WithValidId_ShouldReturnAccommodationResponse() {
        Long id = 1L;
        Accommodation accommodation = getAccommodation();
        AccommodationResponseDto expected = getAccommodationResponseDto();

        when(accommodationRepository.findById(id)).thenReturn(Optional.of(accommodation));
        when(accommodationMapper.toDto(accommodation)).thenReturn(expected);

        AccommodationResponseDto actual = accommodationService.getAccommodationById(id);
        assertEquals(expected, actual);
        verify(accommodationRepository).findById(id);
    }

    @Test
    @DisplayName("Get Accommodation when invalid id provided and should throw exception")
    void getAccommodationById_WithInvalidId_ShouldThrownEntityNotFoundException() {
        Long id = -10L;
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> accommodationService.getAccommodationById(id));
        assertEquals("Accommodation not found by id: " + id, exception.getMessage());
    }

    @Test
    @DisplayName("Update Accommodation when valid id provided")
    void updateAccommodation_WithValidIdAndRequest_ShouldUpdateAccommodation() {
        Long id = 1L;
        Accommodation accommodation = getAnotherAccommodation();
        AccommodationResponseDto expected = getAccommodationResponseDto();
        AccommodationRequestDto requestDto = getAccommodationRequestDto();

        when(accommodationRepository.findById(id)).thenReturn(Optional.of(accommodation));
        when(accommodationMapper.toDto(accommodation)).thenReturn(expected);
        doNothing().when(accommodationMapper).updateAccommodation(accommodation, requestDto);

        AccommodationResponseDto actual = accommodationService.updateAccommodation(id, requestDto);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Update Accommodation when invalid id provided")
    void updateAccommodation_WithInvalidId_ShouldThrownEntityNotFoundException() {
        Long id = -10L;
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> accommodationService.updateAccommodation(id, getAccommodationRequestDto()));

        assertEquals("Accommodation not found by id: " + id, exception.getMessage());
    }

    @Test
    @DisplayName("Delete Accommodation when invalid id provided")
    void deleteAccommodation_WithValidId_ShouldDeleteData() {
        Long id = 1L;
        when(accommodationRepository.findById(id)).thenReturn(Optional.of(getAccommodation()));
        accommodationService.deleteAccommodation(id);
        verify(accommodationRepository).deleteById(id);
        verify(accommodationRepository).findById(id);
    }

    @Test
    @DisplayName("Delete Accommodation when invalid id provided")
    void deleteAccommodation_WithInValidId_ShouldThrownEntityNotFoundException() {
        Long id = -10L;
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> accommodationService.deleteAccommodation(id));
        assertEquals("Accommodation not found by id: " + id, exception.getMessage());
    }
}
