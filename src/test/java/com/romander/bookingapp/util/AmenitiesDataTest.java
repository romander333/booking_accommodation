package com.romander.bookingapp.util;

import java.util.ArrayList;
import java.util.List;

public class AmenitiesDataTest {

    public static List<String> getAmenities() {
        List<String> amenities = new ArrayList<>();
        amenities.add("Two big bedroom with one toilet and with light bathroom");
        return amenities;
    }

    public static List<String> getAnotherAmenities() {
        List<String> amenities = new ArrayList<>();
        amenities
                .add("Three more or less big bedroom with two toilet and with two dark bathroom");
        return amenities;
    }
}
