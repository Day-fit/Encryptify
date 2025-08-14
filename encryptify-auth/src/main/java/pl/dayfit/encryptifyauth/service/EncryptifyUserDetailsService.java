package pl.dayfit.encryptifyauth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifyauth.helper.HashHelper;
import pl.dayfit.encryptifyauthlib.principal.UserDetailsImpl;
import pl.dayfit.encryptifyauth.cacheservice.EncryptifyUserCacheService;
import pl.dayfit.encryptifyauth.entity.EncryptifyUser;

import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class EncryptifyUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final EncryptifyUserCacheService encryptifyUserCacheService;
    private final HashHelper hashHelper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Function<String, EncryptifyUser> resolver = username.contains("@")
                ? this::loadByEmail
                : this::loadByUsername;

        EncryptifyUser user = resolver.apply(username);

        return new UserDetailsImpl(
                user.getUsername(),
                user.getPassword(),
                user.isBanned(),
                user.isEnabled(),
                user.getRoles()
                        .stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList(),
                user.getId()
        );
    }

    private EncryptifyUser loadByUsername(String username) throws UsernameNotFoundException
    {
        return encryptifyUserCacheService.getUserByUsername(username);
    }

    /**
     * Finds a user by email using a hashed lookup
     * @param rawEmail Raw form of an email
     * @return Entity of a user that is associated with given email
     * @throws UsernameNotFoundException when no user has been found
     */
    private EncryptifyUser loadByEmail(String rawEmail) throws UsernameNotFoundException
    {
        List<EncryptifyUser> users = encryptifyUserCacheService.getUserByEmailLookup(hashHelper.generateEmailLookup(rawEmail)); //Collisions are possible, but the result should be small anyway

        return users.stream()
                .filter(user -> passwordEncoder.matches(rawEmail, user.getEmailProof()))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("Account does not exist or password does not match"));
    }
}
