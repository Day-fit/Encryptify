package pl.dayfit.encryptifycore.dto;

import pl.dayfit.encryptifycore.type.ActivityType;

import java.time.Instant;
import java.util.UUID;

public record FolderStreamRequestDto(
        UUID userId,
        String name,
        int subfolderCount,
        int fileCount,
        Long size,
        ActivityType activityType,
        Instant timestamp
)
implements FileSystemStreamRequestDto {}
