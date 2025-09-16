package pl.dayfit.encryptifyauth.event;

import java.util.UUID;

public record UserReadyForSetupEvent(UUID userId, String bucketName) {
}
