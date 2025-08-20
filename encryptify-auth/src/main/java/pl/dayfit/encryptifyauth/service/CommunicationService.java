package pl.dayfit.encryptifyauth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifyauth.event.JwtKeyRotatedEvent;
import pl.dayfit.encryptifyauthlib.dto.JwksRotationEvent;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CommunicationService {
    private final RabbitTemplate rabbitTemplate;

    @EventListener
    public void handleJwtKeyRotatedEvent(JwtKeyRotatedEvent ignored)
    {
        JwksRotationEvent event = new JwksRotationEvent(
                Instant.now()
        );

        rabbitTemplate.convertAndSend(
                "auth.rotation",
                "auth.key-changed",
                event
        );
    }
}
