package pl.dayfit.encryptifyemail.event;

import java.time.Instant;

public record EmailAuthenticatedEvent(String username, Instant verificationDate) {
}
