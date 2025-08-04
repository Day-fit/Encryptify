package pl.dayfit.encryptifyauthlib.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;

public class JwtAuthenticationTokenCandidate extends AbstractAuthenticationToken {
    private final Principal principal;
    private final UserDetails userDetails;

    public JwtAuthenticationTokenCandidate(UserDetails userDetails, Principal principal) {
        super(null);
        setAuthenticated(false);

        this.principal = principal;
        this.userDetails = userDetails;
    }

    @Override
    public Object getCredentials() {
        return userDetails.getPassword();
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public UserDetails getDetails()
    {
        return userDetails;
    }
}
