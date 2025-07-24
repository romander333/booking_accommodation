package com.romander.bookingapp.service;

import com.romander.bookingapp.dto.accommodation.AccommodationRequestDto;
import com.romander.bookingapp.dto.accommodation.AccommodationResponseDto;
import com.romander.bookingapp.exception.EntityNotFoundException;
import com.romander.bookingapp.mapper.AccommodationMapper;
import com.romander.bookingapp.model.Accommodation;
import com.romander.bookingapp.repository.AccommodationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final AccommodationMapper accommodationMapper;

    public AccommodationResponseDto createAccommodation(AccommodationRequestDto accommodationRequestDto) {
        Accommodation accommodation = accommodationMapper.toModel(accommodationRequestDto);
        accommodation = accommodationRepository.save(accommodation);
        return accommodationMapper.toDto(accommodation);
    }

    @Override
    public Page<AccommodationResponseDto> getAccommodations(Pageable pageable) {
        return accommodationRepository.findAllWithAmenities(pageable).map(accommodationMapper::toDto);
    }

    @Override
    public AccommodationResponseDto getAccommodationById(Long id) {
        Accommodation accommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Accommodation not found by id: " + id));
        return accommodationMapper.toDto(accommodation);
    }

    @Override
    @Transactional
    public AccommodationResponseDto updateAccommodation(Long id, AccommodationRequestDto requestDto) {
        Accommodation accommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Accommodation not found by id: " + id));
        accommodationMapper.updateAccommodation(accommodation, requestDto);

        return accommodationMapper.toDto(accommodation);
    }

    @Override
    public void deleteAccommodation(Long id) {
        accommodationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Accommodation not found by id: " + id));
        accommodationRepository.deleteById(id);
    }
}
