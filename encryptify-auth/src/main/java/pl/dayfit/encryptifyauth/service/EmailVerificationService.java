package pl.dayfit.encryptifyauth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifyauth.cacheservice.EncryptifyUserCacheService;
import pl.dayfit.encryptifyauth.dto.EmailAuthenticatedDTO;
import pl.dayfit.encryptifyauth.event.UserRegisteredEvent;
import pl.dayfit.encryptifyauth.entity.EncryptifyUser;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    private final EncryptifyUserCacheService encryptifyUserCacheService;
    private final RabbitTemplate rabbitTemplate;

    @EventListener
    public void handleVerificationSending(UserRegisteredEvent event)
    {
        rabbitTemplate.convertAndSend
                (
                    "user.events",
                        "user.created",
                        event
                );
    }

    @RabbitListener(queues = "service.auth")
    public void handleVerificationConfirmation(EmailAuthenticatedDTO dto)
    {
        EncryptifyUser user = encryptifyUserCacheService.getUserByUsername(dto.username());
        user.setEmailVerificationDate(dto.verificationDate());
        user.setEnabled(true);

        encryptifyUserCacheService.saveUser(user);
    }
}
