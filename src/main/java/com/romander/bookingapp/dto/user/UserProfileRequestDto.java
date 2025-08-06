package com.romander.bookingapp.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserProfileRequestDto {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
}
