package pl.dayfit.encryptifycore.dto;

import pl.dayfit.encryptifycore.type.ActivityType;
import pl.dayfit.encryptifycore.type.TargetType;

import java.time.Instant;
import java.util.UUID;

public record ActivityStreamDto(UUID userId,
                                String targetName,
                                String targetSize,
                                ActivityType activityType,
                                TargetType targetType,
                                Instant timestamp
) {}
