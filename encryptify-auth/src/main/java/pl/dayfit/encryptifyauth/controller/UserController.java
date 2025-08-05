package pl.dayfit.encryptifyauth.controller;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.dayfit.encryptifyauth.configuration.CookieConfigurationProperties;
import pl.dayfit.encryptifyauth.dto.LoginRequestDTO;
import pl.dayfit.encryptifyauth.dto.RegisterRequestDTO;
import pl.dayfit.encryptifyauth.service.JwtService;
import pl.dayfit.encryptifyauth.service.UserService;
import pl.dayfit.encryptifyauthlib.type.JwtTokenType;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;
    private final CookieConfigurationProperties configurationProperties;

    private int refreshTokenValidityDays;
    private String refreshTokenName;
    private int accessTokenValidityMinutes;
    private String accessTokenName;
    private boolean isSecured;

    @PostConstruct
    private void init()
    {
        refreshTokenValidityDays =  configurationProperties.getRefreshTokenValidityDays();
        refreshTokenName = configurationProperties.getRefreshTokenName();
        accessTokenValidityMinutes = configurationProperties.getAccessTokenValidityMinutes();
        accessTokenName = configurationProperties.getAccessTokenName();
        isSecured = configurationProperties.isSecured();
    }

    @PostMapping("/api/v1/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO dto, HttpServletResponse response)
    {
        long userId = userService.handleLogin(dto);

        ResponseCookie accessTokenCookie = ResponseCookie.from(
                    accessTokenName,
                    jwtService.generateToken(userId, accessTokenValidityMinutes * 60 * 1000L, JwtTokenType.ACCESS_TOKEN)
                )
                .httpOnly(true)
                .secure(isSecured)
                .path("/")
                .maxAge(Duration.ofMinutes(accessTokenValidityMinutes))
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from(
                    refreshTokenName,
                    jwtService.generateToken(userId, refreshTokenValidityDays * 24 * 60 * 60 * 1000L,  JwtTokenType.REFRESH_TOKEN)
                )
                .httpOnly(true)
                .secure(isSecured)
                .path("/")
                .maxAge(Duration.ofDays(refreshTokenValidityDays))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity.ok(Map.of("message", "Login went successfully"));
    }

    @PostMapping("/api/v1/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequestDTO dto)
    {
        userService.handleRegister(dto);
        return ResponseEntity.ok(Map.of("message", "Sign up successfully, email with confirmation link was sent to your email"));
    }

    @PostMapping("/api/v1/logout")
    public ResponseEntity<?> logout(HttpServletResponse response)
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

        response.addHeader(HttpHeaders.SET_COOKIE, accessToken.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshToken.toString());

        return ResponseEntity.ok(Map.of("message", "Logout successfully"));
    }

    @PostMapping("/api/v1/refresh")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response)
    {
        Cookie[] cookies = request.getCookies();

        if (cookies == null)
        {
            throw new BadCredentialsException("Missing cookie header");
        }

        Cookie refreshTokenCookie = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(refreshTokenName))
                .findFirst()
                .orElseThrow(() -> new BadCredentialsException("Could not find refresh token"));

        ResponseCookie accessTokenCookie = ResponseCookie.from(accessTokenName, userService.handleAccessTokenRefresh(refreshTokenCookie.getValue(), refreshTokenValidityDays * 24 * 60 * 60 * 1000L))
                .httpOnly(true)
                .secure(isSecured)
                .path("/")
                .maxAge(Duration.ofMinutes(accessTokenValidityMinutes))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

        return ResponseEntity.ok(Map.of("message", "Refresh went successfully"));
    }
}
