package pl.dayfit.encryptifyemail.event;

public record EmailVerificationCodeEvent
        (
            String username,
            String email,
            int code
        )
{}
