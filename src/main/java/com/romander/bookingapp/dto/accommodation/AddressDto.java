package com.romander.bookingapp.dto.accommodation;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AddressDto {
    private String country;
    private String city;
    private String zipcode;
}
