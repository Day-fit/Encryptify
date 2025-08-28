package pl.dayfit.encryptifycore.dto;

import java.time.Instant;

public record FileResponseDto(String name, String fileSize, String parent, Instant uploadDate) {
}
