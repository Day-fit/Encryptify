package pl.dayfit.encryptifycore.dto;

public record FileRequestDto(
        String name,
        String base64Content,
        String parent
)
{}
