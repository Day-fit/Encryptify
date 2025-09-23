package pl.dayfit.encryptifycore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.rabbit.stream.producer.RabbitStreamTemplate;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifycore.dto.FileSystemStreamRequestDto;

@Service
@RequiredArgsConstructor
public class StatisticsCommunicationService {
    private final RabbitStreamTemplate rabbitStreamTemplate;

    public void sendActivity(FileSystemStreamRequestDto message)
    {
        rabbitStreamTemplate
                .convertAndSend(message);
    }
}
