package pl.dayfit.encryptify.stats.dto;

import pl.dayfit.encryptify.stats.type.ActivityType;
import pl.dayfit.encryptify.stats.type.TargetType;

import java.time.Instant;
import java.util.UUID;

public record ActivityStreamDto(UUID userId,
                                String targetName,
                                String targetSize,
                                ActivityType activityType,
                                TargetType targetType,
                                Instant timestamp
) {}
