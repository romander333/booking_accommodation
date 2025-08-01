package com.romander.bookingapp.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Data
@Accessors(chain = true)
public class SignInRequestDto {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Length(min = 8, max = 16)
    private String password;
}
