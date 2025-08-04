package pl.dayfit.encryptifyauthlib.authenticationprovider;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
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

        additionalChecks(candidate.getDetails());

        return new JwtAuthenticationToken(jwtClaimsService.getRoles((String) candidate.getCredentials()), (Principal) candidate.getPrincipal());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationTokenCandidate.class.isAssignableFrom(authentication);
    }

    private void additionalChecks(UserDetails userDetails)
    {
        checkIfBanned(userDetails);
        checkIfDisabled(userDetails);
    }

    private void checkIfBanned(UserDetails details)
    {
        if (!details.isAccountNonLocked())
        {
            throw new LockedException("Account is banned");
        }
    }

    private void checkIfDisabled(UserDetails details)
    {
        if (!details.isEnabled())
        {
            throw new DisabledException("User is not enabled");
        }
    }
}
