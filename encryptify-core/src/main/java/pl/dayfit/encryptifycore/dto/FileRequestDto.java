package pl.dayfit.encryptifycore.dto;

public record FileRequestDto(
        String name,
        Long folderId,
        String publicKey
)
{}
