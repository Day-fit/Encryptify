package pl.dayfit.encryptifyauth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifyauth.cacheservice.EncryptifyUserCacheService;
import pl.dayfit.encryptifyauth.event.EmailAuthenticatedEvent;
import pl.dayfit.encryptifyauth.event.EmailVerificationCodeEvent;
import pl.dayfit.encryptifyauth.event.UserRegisteredEvent;
import pl.dayfit.encryptifyauth.entity.EncryptifyUser;
import pl.dayfit.encryptifyauth.exception.NoUniqueCodeException;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class EmailCommunicationService {
    private final EncryptifyUserCacheService encryptifyUserCacheService;
    private final RabbitTemplate rabbitTemplate;
    private final SecureRandom secureRandom;

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Handles process of sending a message to rabbitMQ when user is registered
     * @param event trigger event
     */
    @EventListener
    public void handleVerificationSending(UserRegisteredEvent event)
    {
        rabbitTemplate.convertAndSend
                (
                        "user.events",
                        "user.created",
                        new EmailVerificationCodeEvent
                                (
                                        event.username(),
                                        event.receiverEmail(),
                                        generateCode()
                                )
                );
    }

    @RabbitListener(queues = "service.auth")
    public void handleVerificationConfirmation(EmailAuthenticatedEvent dto)
    {
        EncryptifyUser user = encryptifyUserCacheService.getUserByUsername(dto.username());
        user.setEmailVerificationDate(dto.verificationDate());
        user.setEnabled(true);

        encryptifyUserCacheService.saveUser(user);
    }

    /**
     * Generates unique verification code
     * @return unique 6-digits long code
     */
    private int generateCode()
    {
        int MAX_RETRY_COUNT = 200;
        for (int i = 0; i < MAX_RETRY_COUNT; i++)
        {
            int MIN_6_DIGITS_NUMBER = 100000;
            int MAX_6_DIGITS_NUMBER = 999999;

            int potentialCode = secureRandom
                    .nextInt(
                            MIN_6_DIGITS_NUMBER,
                            MAX_6_DIGITS_NUMBER
                    );

            //TODO: ensure that code will be unique. also save code in redis.
            return potentialCode;
        }

        throw new NoUniqueCodeException("There are too many registration requests. Try again later.");
    }

    /**
     * Checks if given code already exists in redis memory
     * @param code represents a value to check
     * @return true if code is unique, false otherwise
     */
    private boolean isUniqueCode(String code)
    {
        return redisTemplate.opsForValue().get(code) == null;
    }
}
