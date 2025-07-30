package com.romander.bookingapp.mapper;

import com.romander.bookingapp.config.MapperConfig;
import com.romander.bookingapp.dto.accommodation.AccommodationRequestDto;
import com.romander.bookingapp.dto.accommodation.AccommodationResponseDto;
import com.romander.bookingapp.model.Accommodation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface AccommodationMapper {

    @Mapping(target = "id", ignore = true)
    void updateAccommodation(@MappingTarget Accommodation accommodation,
                             AccommodationRequestDto accommodationRequestDto);

    Accommodation toModel(AccommodationRequestDto requestDto);

    AccommodationResponseDto toDto(Accommodation accommodation);
}
