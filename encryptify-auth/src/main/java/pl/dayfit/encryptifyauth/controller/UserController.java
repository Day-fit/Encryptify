package pl.dayfit.encryptifyauth.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.dayfit.encryptifyauth.configuration.CookieConfigurationProperties;
import pl.dayfit.encryptifyauth.dto.LoginRequestDTO;
import pl.dayfit.encryptifyauth.service.JwtService;
import pl.dayfit.encryptifyauth.service.UserService;
import pl.dayfit.encryptifydata.cacheservice.UserCacheService;

import java.time.Duration;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final CookieConfigurationProperties cookieConfigurationProperties;
    private final UserService userService;
    private final UserCacheService userCacheService;
    private final JwtService jwtService;

    private int accessTokenValidityMinutes;
    private int refreshTokenValidityDays;
    private String refreshTokenName;
    private String accessTokenName;
    private boolean isSecured;

    @PostConstruct
    private void init()
    {
        accessTokenValidityMinutes = cookieConfigurationProperties.getAccessTokenValidityMinutes();
        refreshTokenValidityDays = cookieConfigurationProperties.getRefreshTokenValidityDays();
        refreshTokenName = cookieConfigurationProperties.getRefreshTokenName();
        accessTokenName = cookieConfigurationProperties.getAccessTokenName();
        isSecured = cookieConfigurationProperties.isSecured();

        if (!isSecured)
        {
            log.warn("Using unsecured cookies is only acceptable in dev/test environment");
        }
    }

    @PostMapping("/api/v1/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto, ServletServerHttpResponse response)
    {
        userService.handleLogin(dto);

        long userId = userCacheService.getUserIdByIdentifier(dto.identifier());

        ResponseCookie accessTokenCookie = ResponseCookie.from(
                    accessTokenName,
                    jwtService.generateToken(userId, accessTokenValidityMinutes * 60 * 1000L)
                )
                .httpOnly(true)
                .secure(isSecured)
                .path("/")
                .maxAge(Duration.ofMinutes(accessTokenValidityMinutes))
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from(
                    refreshTokenName,
                    jwtService.generateToken(userId, refreshTokenValidityDays * 24 * 60 * 60 * 1000L)
                )
                .httpOnly(true)
                .secure(isSecured)
                .path("/")
                .maxAge(Duration.ofDays(refreshTokenValidityDays))
                .build();

        response.getHeaders()
                .add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.getHeaders()
                .add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity.ok(Map.of("message", "Login went successfully"));
    }

    @PostMapping("/api/v1/logout")
    public ResponseEntity<?> logout(ServletServerHttpResponse response)
    {
        ResponseCookie accessToken = ResponseCookie.from(accessTokenName, "")
                .httpOnly(true)
                .secure(isSecured)
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie refreshToken = ResponseCookie.from(refreshTokenName, "")
                .httpOnly(true)
                .secure(isSecured)
                .path("/")
                .maxAge(0)
                .build();

        response.getHeaders()
                .add(HttpHeaders.SET_COOKIE, accessToken.toString());

        response.getHeaders()
                .add(HttpHeaders.SET_COOKIE, refreshToken.toString());

        return ResponseEntity.ok(Map.of("message", "Logout successfully"));
    }

    @PostMapping("/api/v1/refresh")
    public ResponseEntity<?> refreshAccessToken(ServletServerHttpResponse response)
    {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
