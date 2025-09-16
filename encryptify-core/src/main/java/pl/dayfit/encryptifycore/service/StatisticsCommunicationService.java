package pl.dayfit.encryptifycore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.rabbit.stream.producer.RabbitStreamTemplate;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifycore.dto.ActivityStreamDto;

@Service
@RequiredArgsConstructor
public class StatisticsCommunicationService {
    private final RabbitStreamTemplate rabbitStreamTemplate;

    public void sendActivity(ActivityStreamDto message)
    {
        rabbitStreamTemplate
                .convertAndSend(message);
    }
}
