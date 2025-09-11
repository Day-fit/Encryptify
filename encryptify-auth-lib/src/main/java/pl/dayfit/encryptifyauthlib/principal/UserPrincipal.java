package pl.dayfit.encryptifyauthlib.principal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.Principal;

@RequiredArgsConstructor
public class UserPrincipal implements Principal {
    private final String username;

    @Getter
    private final String bucketName;

    @Override
    public String getName() {
        return username;
    }
}
