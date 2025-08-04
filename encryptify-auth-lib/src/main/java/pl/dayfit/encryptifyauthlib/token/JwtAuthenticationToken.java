package pl.dayfit.encryptifyauthlib.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;
import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private final Principal principal;

    public JwtAuthenticationToken(Collection<? extends GrantedAuthority> roles, Principal principal) {
        super(roles);
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
