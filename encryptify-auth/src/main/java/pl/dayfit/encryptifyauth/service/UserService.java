package pl.dayfit.encryptifyauth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifyauth.authenticationprovider.UserDetailsAuthenticationProvider;
import pl.dayfit.encryptifyauth.dto.LoginRequestDTO;
import pl.dayfit.encryptifyauth.dto.RegisterRequestDTO;
import pl.dayfit.encryptifyauth.token.UserDetailsTokenCandidate;
import pl.dayfit.encryptifydata.cacheservice.UserCacheService;
import pl.dayfit.encryptifydata.entity.EncryptifyUser;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserCacheService cacheService;
    private final UserDetailsAuthenticationProvider authenticationProvider;
    private final PasswordEncoder passwordEncoder;

    public void handleLogin(LoginRequestDTO dto)
    {
        authenticationProvider.authenticate(new UserDetailsTokenCandidate(dto.identifier(), dto.password()));
    }

    public void handleRegister(RegisterRequestDTO dto)
    {
        cacheService.saveUser(new EncryptifyUser(
                        null,
                        dto.email(),
                        dto.username(),
                        passwordEncoder.encode(dto.password()),
                        Instant.now(),
                        false,
                        false,
                        List.of(new SimpleGrantedAuthority("USER"))
                )
        );
    }
}
