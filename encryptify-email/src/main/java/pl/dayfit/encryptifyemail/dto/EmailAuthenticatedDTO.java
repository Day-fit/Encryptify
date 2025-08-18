package pl.dayfit.encryptifyemail.dto;

import java.time.Instant;

public record EmailAuthenticatedDTO(String username, Instant verificationDate) {
}
