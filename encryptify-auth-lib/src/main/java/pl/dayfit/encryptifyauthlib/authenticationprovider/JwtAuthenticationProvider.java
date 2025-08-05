package pl.dayfit.encryptifyauthlib.authenticationprovider;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import pl.dayfit.encryptifyauthlib.service.JwtClaimsService;
import pl.dayfit.encryptifyauthlib.token.JwtAuthenticationToken;
import pl.dayfit.encryptifyauthlib.token.JwtAuthenticationTokenCandidate;

import java.security.Principal;

@Component
@RequiredArgsConstructor
@ConditionalOnBooleanProperty(value = "key-listener.enabled")
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final JwtClaimsService jwtClaimsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationTokenCandidate candidate = (JwtAuthenticationTokenCandidate) authentication;

        if (jwtClaimsService.isExpired((String) authentication.getCredentials()))
        {
            throw new BadCredentialsException("Invalid JWT token");
        }

        return new JwtAuthenticationToken(jwtClaimsService.getRoles((String) candidate.getCredentials()), (Principal) candidate.getPrincipal());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationTokenCandidate.class.isAssignableFrom(authentication);
    }
}
