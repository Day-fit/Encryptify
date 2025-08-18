package pl.dayfit.encryptifyauth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifyauth.event.JwtKeyRotatedEvent;
import pl.dayfit.encryptifyauthlib.dto.PublicKeyRotationDTO;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class CommunicationService {
    private final RabbitTemplate rabbitTemplate;

    @EventListener
    public void handleJwtKeyRotatedEvent(JwtKeyRotatedEvent event)
    {
        PublicKeyRotationDTO dto = new PublicKeyRotationDTO(
                Base64.getEncoder().encode(event.key().getEncoded()),
                event.keyId()
        );

        rabbitTemplate.convertAndSend(
                "auth.rotation",
                "auth.key-changed",
                dto
        );
    }
}
