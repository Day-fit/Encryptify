package pl.dayfit.encryptifyauth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "Identifier cannot be blank")
        String identifier,

        @NotBlank(message = "Password cannot be blank")
        String password
)
{}
