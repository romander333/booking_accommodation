package com.romander.bookingapp.dto.accommodation;

import com.romander.bookingapp.model.Accommodation;
import com.romander.bookingapp.model.Address;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
    @Positive
    private Integer availability;
}
