package pl.dayfit.encryptifyauthlib.authenticationprovider;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import pl.dayfit.encryptifyauthlib.principal.UserPrincipal;
import pl.dayfit.encryptifyauthlib.service.JwtClaimsService;
import pl.dayfit.encryptifyauthlib.token.JwtAuthenticationToken;
import pl.dayfit.encryptifyauthlib.token.JwtAuthenticationTokenCandidate;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final JwtClaimsService jwtClaimsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String accessToken = (String) authentication.getCredentials();
        UUID subject = jwtClaimsService.getSubject(accessToken); //If token is invalid, exception will be thrown

        return new JwtAuthenticationToken(
                jwtClaimsService.getRoles(accessToken),
                new UserPrincipal(
                        subject,
                        jwtClaimsService.getBucketName(accessToken)
                )
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationTokenCandidate
                .class
                .isAssignableFrom(authentication);
    }
}
