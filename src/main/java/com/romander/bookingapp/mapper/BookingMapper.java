package com.romander.bookingapp.mapper;

import com.romander.bookingapp.config.MapperConfig;
import com.romander.bookingapp.dto.booking.BookingRequestDto;
import com.romander.bookingapp.dto.booking.BookingResponseDto;
import com.romander.bookingapp.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookingMapper {

    Booking toModel(BookingRequestDto requestDto);

    @Mapping(target = "accommodationId", source = "accommodation.id")
    @Mapping(target = "userId", source = "user.id")
    BookingResponseDto toDto(Booking booking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateBooking(@MappingTarget Booking booking, BookingRequestDto requestDto);
}
