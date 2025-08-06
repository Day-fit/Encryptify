package pl.dayfit.encryptifyauth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifyauth.authenticationprovider.UserDetailsAuthenticationProvider;
import pl.dayfit.encryptifyauth.cacheservice.EncryptifyUserCacheService;
import pl.dayfit.encryptifyauth.dto.LoginRequestDTO;
import pl.dayfit.encryptifyauth.dto.RegisterRequestDTO;
import pl.dayfit.encryptifyauth.entity.EncryptifyUser;
import pl.dayfit.encryptifyauth.exception.UserAlreadyExistsException;
import pl.dayfit.encryptifyauth.helper.HashHelper;
import pl.dayfit.encryptifyauth.repository.UserRepository;
import pl.dayfit.encryptifyauth.token.UserDetailsToken;
import pl.dayfit.encryptifyauth.token.UserDetailsTokenCandidate;
import pl.dayfit.encryptifyauthlib.service.JwtClaimsService;
import pl.dayfit.encryptifyauthlib.type.JwtTokenType;

import java.time.Instant;
import java.util.List;

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

    /**
     * Handles login logic
     * @return username of the user account
     * @param dto DTO with account credentials
     */
    public String handleLogin(LoginRequestDTO dto)
    {
        UserDetailsToken token = (UserDetailsToken) authenticationProvider.authenticate(new UserDetailsTokenCandidate(dto.identifier(), dto.password()));
        return token.getName();
    }

    public void handleRegister(RegisterRequestDTO dto)
    {
        if(userRepository.existsByEmailHashLookup(dto.email()) || userRepository.existsByUsername(dto.username()))
        {
            throw new UserAlreadyExistsException("User already exists");
        }

        cacheService.saveUser(new EncryptifyUser(
                        null,
                        passwordEncoder.encode(dto.email()),
                        hashHelper.generateEmailLookup( dto.email()),
                        dto.username(),
                        passwordEncoder.encode(dto.password()),
                        Instant.now(),
                        false,
                        false,
                        List.of(new SimpleGrantedAuthority("USER"))
                )
        );
    }

    public String handleAccessTokenRefresh(String refreshToken, long validity)
    {
        String username = jwtClaimsService.getSubject(refreshToken);

        if(checkIfUserIsBanned(username))
        {
            throw new BadCredentialsException("User is banned");
        }

        if (jwtClaimsService.isExpired(refreshToken))
        {
            throw new BadCredentialsException("Refresh token expired");
        }

        return jwtService.generateToken(username, validity, JwtTokenType.ACCESS_TOKEN);
    }

    /**
     * Method that checks if the user is banned or not
     * @param username username of user to check
     * @return true if user is banned, false otherwise
     */
    private boolean checkIfUserIsBanned(String username) {
        EncryptifyUser user = cacheService.getUserByUsername(username);
        return user.isBanned();
    }
}
