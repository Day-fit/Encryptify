package pl.dayfit.encryptifyauth.token;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class UserDetailsTokenCandidate extends AbstractAuthenticationToken {
    @Getter
    private final String identifier;
    private final String password;

    public UserDetailsTokenCandidate(String identifier, String password) {
        super(null);

        this.identifier = identifier;
        this.password = password;

        setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return password;
    }

    @Override
    public String getName()
    {
        return identifier;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
