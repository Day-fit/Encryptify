package pl.dayfit.encryptifyemail.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifyemail.dto.EmailAuthenticatedDTO;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class VerificationService {
    private final JwtClaimsService claimsService;
    private final RabbitTemplate rabbitTemplate;

    public void handleVerification(String verificationToken)
    {
        if (!claimsService.isEmailVerificationToken(verificationToken))
        {
            throw new IllegalArgumentException("Given email verification token is not type of email verification token");
        }

        rabbitTemplate
                .convertAndSend(
                        "user.events",
                        "user.verified",
                        new EmailAuthenticatedDTO(
                                claimsService.getSubject(verificationToken),
                                Instant.now()
                        )
                );
    }
}
