package pl.dayfit.encryptifyauthlib.principal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.UUID;

@RequiredArgsConstructor
public class UserPrincipal implements Principal {
    private final UUID id;

    @Getter
    private final String bucketName;

    @Override
    public String getName() {
        return id.toString();
    }
}
