package com.romander.bookingapp.dto.accommodation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.romander.bookingapp.model.Accommodation;
import com.romander.bookingapp.model.Address;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AccommodationRequestDto {
    @NotNull
    private Accommodation.Type type;
    @NotNull
    private Address location;
    @NotBlank
    private String size;
    @NotEmpty
    private List<String> amenities = new ArrayList<>();
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal dailyRate;
    @NotNull
    @Min(1)
    private Integer availability;

}
