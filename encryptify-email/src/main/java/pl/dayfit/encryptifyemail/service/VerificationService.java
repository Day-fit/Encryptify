package pl.dayfit.encryptifyemail.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifyemail.entity.EmailVerification;
import pl.dayfit.encryptifyemail.event.EmailAuthenticatedEvent;

import java.security.InvalidKeyException;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class VerificationService {
    private final RabbitTemplate rabbitTemplate;
    private final RedisTemplate<String, EmailVerification> redisTemplate;

    /**
     * Handles process of verifying verification code,
     * and sending the EmailAuthenticatedEvent to rabbitMQ
     * @param verificationToken represents code to verify
     * @throws InvalidKeyException if given token does not represent any of known codes
     */
    public void handleVerification(final String verificationToken) throws InvalidKeyException {
        if(verificationToken == null)
        {
            throw new IllegalArgumentException("Verification code cannot be null");
        }

        final int MIN_6_DIGIT_NUMBER = 100000;
        final int MAX_6_DIGIT_NUMBER = 999999;

        if(Integer.parseInt(verificationToken) > MIN_6_DIGIT_NUMBER || Integer.parseInt(verificationToken) < MAX_6_DIGIT_NUMBER)
        {
            throw new IllegalArgumentException("Verification code is not 6 digits long");
        }

        EmailVerification verification = redisTemplate.opsForValue().get(verificationToken);

        if(verification == null)
        {
            throw new InvalidKeyException("Verification code is invalid");
        }

        rabbitTemplate
                .convertAndSend(
                        "user.events",
                        "user.verified",
                        new EmailAuthenticatedEvent(
                                verification.getUsername(),
                                Instant.now()
                        )
                );
    }
}
