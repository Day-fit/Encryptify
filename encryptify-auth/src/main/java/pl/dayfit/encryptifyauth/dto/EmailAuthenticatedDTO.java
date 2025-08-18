package pl.dayfit.encryptifyauth.dto;

import java.time.Instant;

public record EmailAuthenticatedDTO(String username, Instant verificationDate) {
}
