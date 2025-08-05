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

import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final EncryptifyUserCacheService cacheService;
    private final UserRepository userRepository;
    private final UserDetailsAuthenticationProvider authenticationProvider;
    private final PasswordEncoder passwordEncoder;
    private final JwtClaimsService jwtClaimsService;
    private final JwtService jwtService;
    private final HashHelper hashHelper;

    /**
     * Handles login logic
     * @return id of the user account
     * @param dto DTO with account credentials
     */
    public long handleLogin(LoginRequestDTO dto)
    {
        UserDetailsToken token = (UserDetailsToken) authenticationProvider.authenticate(new UserDetailsTokenCandidate(dto.identifier(), dto.password()));
        return token.getUserId();
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
        long id = jwtClaimsService.getUserId(refreshToken);

        if(checkIfUserIsBanned(id))
        {
            throw new BadCredentialsException("User is banned");
        }

        if (jwtClaimsService.isExpired(refreshToken))
        {
            throw new BadCredentialsException("Refresh token expired");
        }

        return jwtService.generateToken(id, validity, JwtTokenType.ACCESS_TOKEN);
    }

    private boolean checkIfUserIsBanned(long userId) {
        EncryptifyUser user = cacheService.getUserById(userId);
        return user.isBanned();
    }
}
