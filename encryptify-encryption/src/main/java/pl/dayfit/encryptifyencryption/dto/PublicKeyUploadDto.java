package pl.dayfit.encryptifyencryption.dto;

import jakarta.validation.constraints.NotNull;
import pl.dayfit.encryptifyencryption.validators.PublicKey;

public record PublicKeyUploadDto(
        @PublicKey
        @NotNull String publicKey,
        @NotNull Long id,
        boolean isDirectory
) {}
