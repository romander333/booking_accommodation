package com.romander.bookingapp.util;

import com.romander.bookingapp.dto.accommodation.AddressDto;
import com.romander.bookingapp.model.Address;

public class AddressDataTest {

    public static Address getAddress() {
        Address address = new Address();
        address.setCountry("Ukraine");
        address.setCity("Lviv");
        address.setZipcode("80193");
        return address;
    }

    public static Address getAnotherAddress() {
        Address address = new Address();
        address.setCountry("Ukraine");
        address.setCity("Kyiv");
        address.setZipcode("90703");
        return address;
    }

    public static AddressDto getAddressDto() {
        return new AddressDto()
                .setCountry("Ukraine")
                .setCity("Lviv")
                .setZipcode("80193");
    }

    public static AddressDto getAnotherAddressDto() {
        return new AddressDto()
                .setCountry("Ukraine")
                .setCity("Kyiv")
                .setZipcode("90703");
    }
}
