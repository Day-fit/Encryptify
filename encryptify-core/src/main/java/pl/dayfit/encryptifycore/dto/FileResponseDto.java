package pl.dayfit.encryptifycore.dto;

import java.time.Instant;

public record FileResponseDto(String name, String uploadedBy, String content, Instant uploadDate) {
}
