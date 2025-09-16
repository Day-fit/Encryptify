package pl.dayfit.encryptify.stats.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.dayfit.encryptify.stats.dto.StatisticsDto;
import pl.dayfit.encryptify.stats.service.StatisticsService;
import pl.dayfit.encryptifyauthlib.principal.UserPrincipal;

@RestController
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;

    @GetMapping("/get")
    public ResponseEntity<StatisticsDto> getStatistics(@AuthenticationPrincipal UserPrincipal userPrincipal)
    {
        return ResponseEntity.ok(
                statisticsService.getStatisticsAsDto(userPrincipal.getName())
        );
    }
}
