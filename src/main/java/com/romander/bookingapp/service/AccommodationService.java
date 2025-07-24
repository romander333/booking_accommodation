package com.romander.bookingapp.service;

import com.romander.bookingapp.dto.accommodation.AccommodationRequestDto;
import com.romander.bookingapp.dto.accommodation.AccommodationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccommodationService {
    AccommodationResponseDto createAccommodation(AccommodationRequestDto requestDto);

    Page<AccommodationResponseDto> getAccommodations(Pageable pageable);

    AccommodationResponseDto getAccommodationById(Long id);

    AccommodationResponseDto updateAccommodation(Long id, AccommodationRequestDto requestDto);

    void deleteAccommodation(Long id);
}
