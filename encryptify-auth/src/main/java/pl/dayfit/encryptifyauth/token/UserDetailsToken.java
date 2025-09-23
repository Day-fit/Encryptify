package pl.dayfit.encryptifyauth.token;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;
import java.util.Collection;
import java.util.UUID;

public class UserDetailsToken extends AbstractAuthenticationToken {
    private final Principal principal;
    @Getter
    private final UUID userId;

    public UserDetailsToken(Collection<? extends GrantedAuthority> authorities, Principal user, UUID userId) {
        super(authorities);
        this.principal = user;
        this.userId = userId;
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
