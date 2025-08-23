package pl.dayfit.encryptifyauth.service;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifyauth.cacheservice.EncryptifyUserCacheService;
import pl.dayfit.encryptifyauth.configuration.EmailCodeConfigurationProperties;
import pl.dayfit.encryptifyauth.event.EmailAuthenticatedEvent;
import pl.dayfit.encryptifyauth.event.EmailVerificationCodeEvent;
import pl.dayfit.encryptifyauth.entity.EncryptifyUser;
import pl.dayfit.encryptifyauth.exception.NoUniqueCodeException;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@EnableConfigurationProperties(EmailCodeConfigurationProperties.class)
public class EmailCommunicationService {
    private final EncryptifyUserCacheService encryptifyUserCacheService;
    private final RabbitTemplate rabbitTemplate;
    private final SecureRandom secureRandom;
    private final EmailCodeConfigurationProperties emailCodeConfigurationProperties;

    @Qualifier("noTransactionsRedisTemplate")
    private final StringRedisTemplate stringRedisTemplate;

    public EmailCommunicationService(EncryptifyUserCacheService encryptifyUserCacheService, RabbitTemplate rabbitTemplate, SecureRandom secureRandom, EmailCodeConfigurationProperties emailCodeConfigurationProperties, StringRedisTemplate stringRedisTemplate) {
        this.encryptifyUserCacheService = encryptifyUserCacheService;
        this.rabbitTemplate = rabbitTemplate;
        this.secureRandom = secureRandom;
        this.emailCodeConfigurationProperties = emailCodeConfigurationProperties;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * Handles process of sending a message to rabbitMQ when user is registered
     * @param username name of the user
     * @param email email address of the user
     */
    public void handleVerificationSending(final String username, final String email)
    {
        rabbitTemplate.convertAndSend
                (
                        "user.events",
                        "user.created",
                        new EmailVerificationCodeEvent
                                (
                                        username,
                                        email,
                                        generateCode(username)
                                )
                );
    }

    /**
     * Handles process of enabling user's account when email verification is successful
     * @param event trigger event coming from rabbitMQ
     */
    @RabbitListener(queues = "service.auth")
    public void handleVerificationConfirmation(EmailAuthenticatedEvent event)
    {
        EncryptifyUser user = encryptifyUserCacheService.getUserByUsername(event.username());
        user.setEmailVerificationDate(event.verificationDate());
        user.setEnabled(true);

        encryptifyUserCacheService.saveUser(user);
    }

    /**
     * Generates unique verification code and saves it into cache
     * @param username name of user that code is generated for
     * @return unique 6-digits long code
     * @throws NoUniqueCodeException if retry count exceeds MAX_RETRY_COUNT limit
     */
    private int generateCode(String username)
    {
        final int MAX_RETRY_COUNT = 200;
        final int MIN_6_DIGITS_NUMBER = 100000;
        final int MAX_6_DIGITS_NUMBER = 999999;

        for (int i = 0; i < MAX_RETRY_COUNT; i++)
        {
            int potentialCode = secureRandom
                    .nextInt(
                            MIN_6_DIGITS_NUMBER,
                            MAX_6_DIGITS_NUMBER
                    );

            Boolean isUnique = stringRedisTemplate.opsForValue()
                    .setIfAbsent(
                            "code." + potentialCode,
                            username,
                            Duration.ofMinutes(emailCodeConfigurationProperties.getValidityInMinutes())
                    );

            if(Boolean.TRUE.equals(isUnique))
            {
                return potentialCode;
            }
        }

        throw new NoUniqueCodeException("There are too many registration requests. Try again later.");
    }
}
