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
    private final NotificationService notificationService;
    private final AccommodationRepository accommodationRepository;
    private final AccommodationMapper accommodationMapper;

    public AccommodationResponseDto createAccommodation(AccommodationRequestDto accommodationRequestDto) {
        Accommodation accommodation = accommodationMapper.toModel(accommodationRequestDto);
        accommodation = accommodationRepository.save(accommodation);
        notificationService.sendMessage(String.format(
                "🏠 New housing has been created.!\nAccommodation ID: %d\nType: %s\nLocation: %s",
                accommodation.getId(),
                accommodation.getType(),
                accommodation.getLocation()
        ));
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
        Integer oldAvailability  = accommodation.getAvailability();
        accommodationMapper.updateAccommodation(accommodation, requestDto);
        if (requestDto.getAvailability() != null && oldAvailability  < requestDto.getAvailability()) {
            notificationService.sendMessage(String.format(
                    "The housing is vacated!\nAccommodation ID: %d\nNew accessibility: %d",
                    accommodation.getId(),
                    accommodation.getAvailability()
            ));
        }
        return accommodationMapper.toDto(accommodation);
    }

    @Override
    public void deleteAccommodation(Long id) {
        accommodationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Accommodation not found by id: " + id));
        accommodationRepository.deleteById(id);
    }
}
