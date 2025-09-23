package pl.dayfit.encryptify.stats.event;

import java.util.UUID;

public record UserReadyForSetupEvent(UUID userId, String bucketName) {
}
