package pl.dayfit.encryptifyauthlib.principal;

import lombok.RequiredArgsConstructor;

import java.security.Principal;

@RequiredArgsConstructor
public class UserPrincipal implements Principal {
    private final String username;

    @Override
    public String getName() {
        return username;
    }
}
