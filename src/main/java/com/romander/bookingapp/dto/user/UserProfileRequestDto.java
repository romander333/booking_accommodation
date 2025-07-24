package com.romander.bookingapp.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserProfileRequestDto {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
}
