package pl.dayfit.encryptifycore.dto;

import java.time.Instant;

public record FileResponseDto(String name, String uploader, String content, Instant uploadDate) {
}
