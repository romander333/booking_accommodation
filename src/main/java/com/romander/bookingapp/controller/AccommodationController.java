package com.romander.bookingapp.controller;

import com.romander.bookingapp.dto.accommodation.AccommodationRequestDto;
import com.romander.bookingapp.dto.accommodation.AccommodationResponseDto;
import com.romander.bookingapp.service.AccommodationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Accommodation management", description = "Endpoint for managing accommodation")
@RestController
@RequiredArgsConstructor
@RequestMapping("/accommodations")
public class AccommodationController {
    private final AccommodationService accommodationService;

    @Operation(summary = "Create accommodation",
            description = "Create accommodation with request")
    @PreAuthorize("hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public AccommodationResponseDto createAccommodation(
            @RequestBody @Valid AccommodationRequestDto accommodationRequestDto) {
        return accommodationService.createAccommodation(accommodationRequestDto);
    }

    @Operation(summary = "Get accommodations",
            description = "get page accommodations")
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping
    public Page<AccommodationResponseDto> getAccommodations(Pageable pageable) {
        return accommodationService.getAccommodations(pageable);
    }

    @Operation(summary = "Get accommodation",
            description = "Create accommodation by id")
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/{id}")
    public AccommodationResponseDto getAccommodation(@PathVariable Long id) {
        return accommodationService.getAccommodationById(id);
    }

    @Operation(summary = "Update accommodation",
            description = "Update accommodation by id")
    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}")
    public AccommodationResponseDto updateAccommodation(
            @PathVariable Long id,
            @RequestBody @Valid AccommodationRequestDto accommodationRequestDto) {
        return accommodationService.updateAccommodation(id, accommodationRequestDto);
    }

    @Operation(summary = "Delete accommodation",
            description = "Delete accommodation by id")
    @PreAuthorize("hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteAccommodation(@PathVariable Long id) {
        accommodationService.deleteAccommodation(id);
    }


}
