package pl.dayfit.encryptifyauth.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;
import java.util.Collection;

public class UserDetailsToken extends AbstractAuthenticationToken {
    private final Principal principal;

    public UserDetailsToken(Collection<? extends GrantedAuthority> authorities, Principal user) {
        super(authorities);
        this.principal = user;
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
