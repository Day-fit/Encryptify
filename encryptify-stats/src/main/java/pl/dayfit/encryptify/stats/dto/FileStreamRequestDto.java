package pl.dayfit.encryptify.stats.dto;

import pl.dayfit.encryptify.stats.type.ActivityType;

import java.time.Instant;
import java.util.UUID;

public record FileStreamRequestDto(UUID userId,
                                   String name,
                                   Long size,
                                   ActivityType activityType,
                                   Instant timestamp
) implements FileSystemStreamRequestDto {}
