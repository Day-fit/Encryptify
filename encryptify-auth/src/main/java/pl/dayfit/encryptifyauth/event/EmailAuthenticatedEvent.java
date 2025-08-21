package pl.dayfit.encryptifyauth.event;

import java.time.Instant;

public record EmailAuthenticatedEvent(String username, Instant verificationDate) {
}
