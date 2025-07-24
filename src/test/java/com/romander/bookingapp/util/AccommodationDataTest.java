package com.romander.bookingapp.util;

import com.romander.bookingapp.dto.accommodation.AccommodationRequestDto;
import com.romander.bookingapp.dto.accommodation.AccommodationResponseDto;
import com.romander.bookingapp.dto.accommodation.AddressDto;
import com.romander.bookingapp.model.Accommodation;
import com.romander.bookingapp.model.Address;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.romander.bookingapp.util.AddressDataTest.*;
import static com.romander.bookingapp.util.AmenitiesDataTest.getAmenities;
import static com.romander.bookingapp.util.AmenitiesDataTest.getAnotherAmenities;

public class AccommodationDataTest {


    public static Accommodation getAccommodation() {
        Accommodation accommodation = new Accommodation();
        accommodation.setId(1L);
        accommodation.setAmenities(getAmenities());
        accommodation.setLocation(getAddress());
        accommodation.setSize("2 bedroom");
        accommodation.setType(Accommodation.Type.valueOf("APARTMENT"));
        accommodation.setDailyRate(BigDecimal.valueOf(1500.00).setScale(2, BigDecimal.ROUND_HALF_UP));
        accommodation.setAvailability(3);
        return accommodation;
    }

    public static Accommodation getAnotherAccommodation() {
        Accommodation accommodation = new Accommodation();
        accommodation.setId(2L);
        accommodation.setAmenities(getAnotherAmenities());
        accommodation.setLocation(getAnotherAddress());
        accommodation.setSize("3 bedroom");
        accommodation.setType(Accommodation.Type.valueOf("HOUSE"));
        accommodation.setDailyRate(BigDecimal.valueOf(2300.00).setScale(2, BigDecimal.ROUND_HALF_UP));
        accommodation.setAvailability(2);
        return accommodation;
    }

    public static AccommodationRequestDto getAccommodationRequestDto() {
        return new AccommodationRequestDto()
                .setType(Accommodation.Type.valueOf("APARTMENT"))
                .setLocation(getAddress())
                .setSize("2 bedroom")
                .setAvailability(3)
                .setDailyRate(BigDecimal.valueOf(1500.00).setScale(2, BigDecimal.ROUND_HALF_UP))
                .setAmenities(getAmenities());
    }

    public static AccommodationResponseDto getAccommodationResponseDto() {
        return new AccommodationResponseDto()
                .setId(1L)
                .setType(Accommodation.Type.valueOf("APARTMENT"))
                .setLocation(getAddressDto())
                .setSize("2 bedroom")
                .setAvailability(3)
                .setDailyRate(BigDecimal.valueOf(1500.00).setScale(2, BigDecimal.ROUND_HALF_UP))
                .setAmenities(getAmenities());
    }
    public static AccommodationResponseDto getAnotherAccommodationResponseDto() {
        AddressDto addressDto = getAnotherAddressDto();
        return new AccommodationResponseDto()
                .setId(2L)
                .setLocation(addressDto)
                .setSize("3 bedroom")
                .setType(Accommodation.Type.valueOf("HOUSE"))
                .setDailyRate(BigDecimal.valueOf(2300.00).setScale(2, BigDecimal.ROUND_HALF_UP))
                .setAmenities(getAnotherAmenities())
                .setAvailability(2);
    }
}
