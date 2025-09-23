package pl.dayfit.encryptify.stats.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.stream.Consumer;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.OffsetSpecification;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import pl.dayfit.encryptify.stats.configuration.properties.RabbitStreamConfigurationProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptify.stats.dto.*;
import pl.dayfit.encryptify.stats.entity.RecentActivity;
import pl.dayfit.encryptify.stats.entity.UserStatistics;
import pl.dayfit.encryptify.stats.event.UserReadyForSetupEvent;
import pl.dayfit.encryptify.stats.repository.UserStatisticsRepository;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final RabbitStreamConfigurationProperties rabbitStreamConfigurationProperties;
    private final UserStatisticsRepository userStatisticsRepository;
    private final Environment streamEnvironment;
    private final ObjectMapper objectMapper;
    private Consumer streamConsumer;

    @PostConstruct
    public void init()
    {
        streamConsumer = streamEnvironment.consumerBuilder()
                .stream(rabbitStreamConfigurationProperties.getStatsStreamName())
                .offset(OffsetSpecification.next())
                .messageHandler((context, record) -> {
                    try {
                        String json = new String(record.getBodyAsBinary(), StandardCharsets.UTF_8);
                        handleActivity(
                                mapToDto(json)
                        );
                    } catch (Exception e) {
                        log.error("An unexpected error occurred when received message", e);
                    }
                })
                .build();
    }

    @PreDestroy
    public void close()
    {
        if (streamConsumer == null)
        {
            return;
        }

        log.info("Closing RabbitMQ Streams consumer...");
        streamConsumer.close();
    }

    /**
     * Handles creating a fresh entity of UserStatistics after email verification
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
     * @param dto message converted to FileSystemStreamRequestDto containing necessary data
     */
    private void handleActivity(FileSystemStreamRequestDto dto)
    {
        UserStatistics statistics = userStatisticsRepository.findByUserId(dto.userId())
                .orElseThrow(() -> {
                    log.error("Statistics not found for userId {}, should exist (bug?)", dto.userId());
                    return new IllegalStateException("Statistics not found.");
                });

        switch (dto.activityType())
        {
            case UPLOAD ->   {
                long size = statistics.getTotalStorageUsed();
                size += dto.size();
                statistics.setTotalStorageUsed(size);
    
                boolean isFolder = dto instanceof FolderStreamRequestDto;
                long newFileCount = (isFolder ? ((FolderStreamRequestDto) dto).fileCount() : 1) + statistics.getTotalFileCount();
                statistics.setTotalFileCount(newFileCount);
                
                if (!isFolder)
                {
                    break;
                }
                
                long count = statistics.getTotalFolderCount() + ((FolderStreamRequestDto) dto).subfolderCount() + 1;
                statistics.setTotalFolderCount(count);
            }

            case DELETION ->  {
                long size = statistics.getTotalStorageUsed();
                size -= dto.size();

                boolean isFolder = dto instanceof FolderStreamRequestDto;
                if (!isFolder)
                {
                    long count = statistics.getTotalFileCount() - 1;
                    statistics.setTotalStorageUsed(size);
                    statistics.setTotalFileCount(count);
                    break;
                }

                long count = statistics.getTotalFolderCount() - ((FolderStreamRequestDto) dto).subfolderCount() - 1;
                statistics.setTotalFolderCount(count);
                statistics.setTotalStorageUsed(size);
            }

            case CREATION ->   {
                if (dto instanceof FileStreamRequestDto)
                {
                    log.warn("File creation activity received, but it should not be here. (Implementation bug?)");
                    return;
                }

                long count = statistics.getTotalFolderCount() + 1;
                statistics.setTotalFolderCount(count);
            }
        }

        RecentActivity recentActivity = new RecentActivity();

        recentActivity.setActivityTarget(dto.name());
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

        ActivityResponseDto activityResponseDto = null;

        if (recentActivity != null)
        {
            activityResponseDto = new ActivityResponseDto(
                    recentActivity.getActivityTarget(),
                    recentActivity.getActivityType(),
                    recentActivity.getTimestamp()
            );
        }

        return new StatisticsDto(
                userStatistics.getTotalFolderCount(),
                userStatistics.getTotalFileCount(),
                userStatistics.getTotalStorageUsed(),
                activityResponseDto
        );
    }

    private FileSystemStreamRequestDto mapToDto(String json) throws Exception
    {
        JsonNode node = objectMapper.readTree(json);

        return node.has("subfolderCount")
                ? objectMapper.readValue(json, FolderStreamRequestDto.class)
                : objectMapper.readValue(json, FileStreamRequestDto.class);
    }
}