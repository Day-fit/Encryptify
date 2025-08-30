package pl.dayfit.encryptifyauth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifyauth.event.UserReadyForSetupEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioCommunicationService {
    private final RabbitTemplate rabbitTemplate;

    @EventListener
    public void requestMinioSetup(UserReadyForSetupEvent userReadyForSetupEvent)
    {
        rabbitTemplate
                .convertAndSend
                        (
                                "user.events",
                                "user.setup",
                                userReadyForSetupEvent
                        );
    }
}
