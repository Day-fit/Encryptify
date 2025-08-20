package pl.dayfit.encryptifyauth.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifyauth.service.JwtSecretRotationService;

@Service
@EnableScheduling
@RequiredArgsConstructor
public class JwtRotationScheduler {
    private final JwtSecretRotationService jwtSecretRotationService;
    private final long ONE_DAY = 24 * 60 * 60 * 1000;

    @Scheduled(fixedRate = ONE_DAY)
    public void triggerRotation()
    {
        try {
            jwtSecretRotationService.generateNewSecretKey();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
