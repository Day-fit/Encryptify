package pl.dayfit.encryptifyauth.event;

public record EmailVerificationCodeEvent
        (
            String username,
            String email,
            int code
        )
{}
