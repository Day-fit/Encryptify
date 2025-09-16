package pl.dayfit.encryptifyauth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dayfit.encryptifyauth.authenticationprovider.UserDetailsAuthenticationProvider;
import pl.dayfit.encryptifyauth.cacheservice.EncryptifyUserCacheService;
import pl.dayfit.encryptifyauth.configuration.JwtConfigurationProperties;
import pl.dayfit.encryptifyauth.dto.LoginRequestDTO;
import pl.dayfit.encryptifyauth.dto.RegisterRequestDTO;
import pl.dayfit.encryptifyauth.entity.EncryptifyUser;
import pl.dayfit.encryptifyauth.event.UserReadyForSetupEvent;
import pl.dayfit.encryptifyauth.exception.UserAlreadyExistsException;
import pl.dayfit.encryptifyauth.helper.HashHelper;
import pl.dayfit.encryptifyauth.repository.UserRepository;
import pl.dayfit.encryptifyauth.token.UserDetailsToken;
import pl.dayfit.encryptifyauth.token.UserDetailsTokenCandidate;
import pl.dayfit.encryptifyauthlib.service.JwtClaimsService;
import pl.dayfit.encryptifyauthlib.type.JwtTokenType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EncryptifyUserService {
    private final EncryptifyUserCacheService cacheService;
    private final UserRepository userRepository;
    private final UserDetailsAuthenticationProvider authenticationProvider;
    private final PasswordEncoder passwordEncoder;
    private final JwtClaimsService jwtClaimsService;
    private final JwtService jwtService;
    private final HashHelper hashHelper;
    private final EmailCommunicationService emailCommunicationService;
    private final JwtConfigurationProperties jwtConfigurationProperties;
    private final Environment environment;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Handles login logic
     * @return username of the user account
     * @param dto DTO with account credentials
     */
    public String handleLogin(LoginRequestDTO dto)
    {
        UserDetailsToken token = (UserDetailsToken) authenticationProvider.authenticate(
                new UserDetailsTokenCandidate(dto.identifier(), dto.password())
        );
        return token.getName();
    }

    /**
     * Handles the registration logic
     * @param dto dto with register credentials
     * @throws UserAlreadyExistsException if user with given username or email already exists
     */
    @Transactional
    public void handleRegister(RegisterRequestDTO dto)
    {
        String email = dto.email();
        String username = dto.username();
        String bucketName = UUID.randomUUID().toString();

        if(userRepository.existsByEmailHashLookup(email) || userRepository.existsByUsername(username))
        {
            throw new UserAlreadyExistsException("User already exists");
        }

        EncryptifyUser user = cacheService
                .saveUser(new EncryptifyUser(
                        null,
                        passwordEncoder.encode(email),
                        hashHelper.generateEmailLookup(email),
                        username,
                        passwordEncoder.encode(dto.password()),
                        Instant.now(),
                        false,
                        false, //we are waiting for user to verify their email
                        List.of("USER"),
                        null,
                        bucketName
                )
        );

        if (environment.matchesProfiles("no-email"))
        {
            applicationEventPublisher
                    .publishEvent(new UserReadyForSetupEvent(user.getId(), bucketName));
            return;
        }

        emailCommunicationService
                .handleVerificationSending(username, email, bucketName, user.getId());
    }

    /**
     * Generates access token with set validity if refresh token is valid
     * @param refreshToken potential refresh token content
     * @return string form of jwt access token
     */
    public String handleAccessTokenRefresh(String refreshToken)
    {
        UUID userId = jwtClaimsService.getSubject(refreshToken);

        if(checkIfUserIsBanned(userId))
        {
            throw new BadCredentialsException("User is banned");
        }

        if (jwtClaimsService.getTokenType(refreshToken) != JwtTokenType.REFRESH_TOKEN)
        {
            throw new BadCredentialsException("Given token is not an instance of refresh token");
        }

        return jwtService.generateToken
                (
                        userId.toString(),
                        jwtConfigurationProperties
                                .getAccessTokenValidityMinutes(),
                        TimeUnit.MINUTES,
                        JwtTokenType.ACCESS_TOKEN
                );
    }

    /**
     * Method that checks if the user is banned or not
     * @param userId UUID of user to check
     * @return true if user is banned, false otherwise
     */
    private boolean checkIfUserIsBanned(UUID userId) {
        EncryptifyUser user = cacheService.getUserById(userId);
        return user.isBanned();
    }

    /**
     * Method that returns username based on UUID
     * @param uuid user UUID
     * @return username of a user
     */
    public String getUsernameByUUID(UUID uuid) {
        return cacheService.getUserById(uuid)
                .getUsername();
    }
}
