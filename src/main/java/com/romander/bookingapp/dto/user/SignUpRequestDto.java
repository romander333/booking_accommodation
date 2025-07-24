package com.romander.bookingapp.dto.user;

import com.romander.bookingapp.validation.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Password
public class SignUpRequestDto  {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @Email
    private String email;
    @Length(min = 8, max = 16)
    private String password;
    @Length(min = 8, max = 16)
    private String confirmPassword;
}
