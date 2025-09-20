package pl.dayfit.encryptifycore.dto;

import pl.dayfit.encryptifycore.type.ActivityType;

import java.time.Instant;
import java.util.UUID;

public interface FileSystemStreamRequestDto {
    UUID userId();
    String name();
    ActivityType activityType();
    Instant timestamp();
    Long size();
}
