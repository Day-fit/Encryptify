package pl.dayfit.encryptify.stats.dto;

import pl.dayfit.encryptify.stats.type.ActivityType;

import java.time.Instant;
import java.util.UUID;

public record FolderStreamRequestDto(
        UUID userId,
        String name,
        Long subfolderCount,
        Long fileCount,
        Long size,
        ActivityType activityType,
        Instant timestamp
)
implements FileSystemStreamRequestDto {}
