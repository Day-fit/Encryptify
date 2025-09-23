package pl.dayfit.encryptify.stats.dto;

import pl.dayfit.encryptify.stats.type.ActivityType;

import java.time.Instant;
import java.util.UUID;

public interface FileSystemStreamRequestDto {
    UUID userId();
    String name();
    ActivityType activityType();
    Instant timestamp();
    Long size();
}
