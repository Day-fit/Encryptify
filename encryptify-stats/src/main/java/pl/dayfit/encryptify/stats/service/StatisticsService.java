package pl.dayfit.encryptify.stats.service;

import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.OffsetSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import pl.dayfit.encryptify.stats.configuration.properties.RabbitStreamConfigurationProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptify.stats.dto.ActivityResponseDto;
import pl.dayfit.encryptify.stats.dto.ActivityStreamDto;
import pl.dayfit.encryptify.stats.dto.StatisticsDto;
import pl.dayfit.encryptify.stats.entity.RecentActivity;
import pl.dayfit.encryptify.stats.entity.UserStatistics;
import pl.dayfit.encryptify.stats.event.UserReadyForSetupEvent;
import pl.dayfit.encryptify.stats.repository.UserStatisticsRepository;
import pl.dayfit.encryptify.stats.type.TargetType;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final RabbitStreamConfigurationProperties rabbitStreamConfigurationProperties;
    private final UserStatisticsRepository userStatisticsRepository;
    private final Environment environment;

    @PostConstruct
    public void init()
    {
        environment.consumerBuilder()
                .stream(rabbitStreamConfigurationProperties.getStatsStreamName())
                .offset(OffsetSpecification.first())
                .messageHandler((context, record) -> handleActivity(record.getBody()))
                .build()
                .close();
    }

    /**
     * Handles creating fresh entity of UserStatistics after email verification
     * @param event trigger event
     */
    @RabbitListener(queues = "statistics.configurer")
    public void setupStatistics(UserReadyForSetupEvent event)
    {
        UserStatistics userStatistics = new UserStatistics();
        userStatistics.setUserId(event.userId());

        userStatisticsRepository.save(userStatistics);
    }

    /**
     * Handles updating statistics of a user using RabbitMQ Streams
     * @param message message containing necessary data
     */
    private void handleActivity(Object message)
    {
        if (!(message instanceof ActivityStreamDto dto))
        {
            log.error("Invalid message received. (Implementation bug?)");
            return;
        }

        UserStatistics statistics = userStatisticsRepository.findByUserId(dto.userId())
                .orElseThrow(() -> {
                    log.error("Statistics not found for userId {}, should exist (bug?)", dto.userId());
                    return new IllegalStateException("Statistics not found.");
                });

        switch (dto.activityType())
        {
            case UPLOAD ->   {
                long size = statistics.getTotalStorageUsed();
                size += dto.targetSize();

                if (TargetType.FILE.equals(dto.targetType()))
                {
                    long count = statistics.getTotalFileCount() + 1;
                    statistics.setTotalStorageUsed(size);
                    statistics.setTotalFileCount(count);
                    break;
                }

                long count = statistics.getTotalFolderCount() + dto.targetValue();
                statistics.setTotalFolderCount(count);
                statistics.setTotalStorageUsed(size);
            }

            case DOWNLOAD ->  {
                long size = statistics.getTotalStorageUsed();
                size -= dto.targetSize();

                if (TargetType.FILE.equals(dto.targetType()))
                {
                    long count = statistics.getTotalFileCount() - 1;
                    statistics.setTotalStorageUsed(size);
                    statistics.setTotalFileCount(count);
                    break;
                }

                long count = statistics.getTotalFolderCount() - dto.targetValue();
                statistics.setTotalFolderCount(count);
                statistics.setTotalStorageUsed(size);
            }

            case CREATION ->   {
                if (TargetType.FOLDER.equals(dto.targetType()))
            }
        }

        RecentActivity recentActivity = new RecentActivity();

        recentActivity.setActivityTarget(dto.targetName());
        recentActivity.setActivityType(dto.activityType());
        recentActivity.setTimestamp(dto.timestamp());

        statistics.setRecentActivity(recentActivity);

        userStatisticsRepository.save(statistics);
    }

    public StatisticsDto getStatisticsAsDto(String uuid) {
        UserStatistics userStatistics = userStatisticsRepository.findByUserId(UUID.fromString(uuid))
                .orElseThrow(() -> {
                    log.error("Statistics not found for userId {}", uuid);
                    return new IllegalStateException("Statistics not found.");
                });

        RecentActivity recentActivity = userStatistics.getRecentActivity();

        return new StatisticsDto(
                userStatistics.getTotalFolderCount(),
                userStatistics.getTotalFileCount(),
                userStatistics.getTotalStorageUsed(),
                new ActivityResponseDto(
                        recentActivity.getActivityTarget(),
                        recentActivity.getActivityType(),
                        recentActivity.getTimestamp()
                )
        );
    }
}
