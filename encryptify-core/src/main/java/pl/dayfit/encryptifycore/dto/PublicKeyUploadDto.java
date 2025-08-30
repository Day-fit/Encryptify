package pl.dayfit.encryptifycore.dto;

public record PublicKeyUploadDto(
        String publicKey,
        Long id,
        boolean isDirectory
) {}
