package pl.dayfit.encryptify.stats.dto;

import pl.dayfit.encryptify.stats.type.ActivityType;

import java.time.Instant;

public record ActivityResponseDto(
        String targetName,
        ActivityType type,
        Instant timestamp
) {}
