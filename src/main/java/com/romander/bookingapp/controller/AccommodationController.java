package com.romander.bookingapp.controller;

import com.romander.bookingapp.dto.accommodation.AccommodationRequestDto;
import com.romander.bookingapp.dto.accommodation.AccommodationResponseDto;
import com.romander.bookingapp.service.AccommodationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accommodations")
public class AccommodationController {
    private final AccommodationService accommodationService;

    @PreAuthorize("hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public AccommodationResponseDto createAccommodation(
            @RequestBody @Valid AccommodationRequestDto accommodationRequestDto) {
        return accommodationService.createAccommodation(accommodationRequestDto);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping
    public Page<AccommodationResponseDto> getAccommodations(Pageable pageable) {
        return accommodationService.getAccommodations(pageable);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/{id}")
    public AccommodationResponseDto getAccommodation(@PathVariable Long id) {
        return accommodationService.getAccommodationById(id);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}")
    public AccommodationResponseDto updateAccommodation(
            @PathVariable Long id,
            @RequestBody @Valid AccommodationRequestDto accommodationRequestDto) {
        return accommodationService.updateAccommodation(id, accommodationRequestDto);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteAccommodation(@PathVariable Long id) {
        accommodationService.deleteAccommodation(id);
    }


}
