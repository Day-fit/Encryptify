package pl.dayfit.encryptifyemail.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifyemail.event.EmailAuthenticatedEvent;
import pl.dayfit.encryptifyemail.exception.InvalidCodeException;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    private final StringRedisTemplate stringRedisTemplate;
    private final RabbitTemplate rabbitTemplate;

    public void handleCodeVerification(String code) {
        String key = "code." + code;

        String username = stringRedisTemplate
                .opsForValue()
                .get(key);

        if (username == null)
        {
            log.debug("User tried to confirm email with invalid code");
            throw new InvalidCodeException("Given code is incorrect. Please try again.");
        }

        log.debug("User {} confirmed email successfully", username);
        rabbitTemplate
                .convertAndSend(
                        "service.auth",
                        new EmailAuthenticatedEvent
                                (
                                        username,
                                        Instant.now()
                                )
                );

        stringRedisTemplate
                .delete(key);
    }
}
