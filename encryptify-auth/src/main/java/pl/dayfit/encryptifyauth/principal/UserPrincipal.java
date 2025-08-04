package pl.dayfit.encryptifyauth.principal;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;

@RequiredArgsConstructor
public class UserPrincipal implements Principal {
    private final UserDetails userDetails;

    @Override
    public String getName() {
        return userDetails.getUsername();
    }
}
