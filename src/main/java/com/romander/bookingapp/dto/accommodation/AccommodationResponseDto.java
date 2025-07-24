package com.romander.bookingapp.dto.accommodation;

import com.romander.bookingapp.model.Accommodation;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class AccommodationResponseDto {
    private Long id;
    private Accommodation.Type type;
    private AddressDto location;
    private String size;
    private List<String> amenities = new ArrayList<>();
    private BigDecimal dailyRate;
    private Integer availability;
}
