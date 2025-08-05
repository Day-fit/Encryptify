package pl.dayfit.encryptifyauth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
        @Email(message = "Given email in incorrect")
        @NotBlank(message = "Email cannot be blank")
        @Size(min = 6, max = 254, message = "Length of the email must be in range of 6-254")
        String email,

        @Size(min = 3, max = 20, message = "Length of the username must be in range of 3-20")
        @Pattern(regexp = "^[A-Za-z0-9_.]+$", message = "Username can contain alphanumeric characters and \"_\", \".\" ")
        @NotBlank(message = "Username cannot be blank")
        String username,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, max = 64, message = "Length of the password must be in range of 8-64")
        String password
) {}
