package pl.dayfit.encryptifycore.dto;

public record FileRequestDto(
        String name,
        String base64Content,
        Long folderId,
        String publicKey
)
{}
