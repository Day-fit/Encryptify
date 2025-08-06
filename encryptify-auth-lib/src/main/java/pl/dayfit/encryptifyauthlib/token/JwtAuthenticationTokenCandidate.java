package pl.dayfit.encryptifyauthlib.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class JwtAuthenticationTokenCandidate extends AbstractAuthenticationToken {
    private final String accessToken;

    public JwtAuthenticationTokenCandidate(String accessToken) {
        super(null);
        this.accessToken = accessToken;
        setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return accessToken;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
