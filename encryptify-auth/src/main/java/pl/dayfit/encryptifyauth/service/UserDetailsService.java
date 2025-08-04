package pl.dayfit.encryptifyauth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifyauth.principal.UserDetailsImpl;
import pl.dayfit.encryptifydata.cacheservice.UserCacheService;
import pl.dayfit.encryptifydata.entity.EncryptifyUser;

@Service
@RequiredArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UserCacheService userCacheService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        EncryptifyUser user = userCacheService.getUserByIdentifier(username);
        return new UserDetailsImpl(user.getUsername(), user.getPassword(), user.isBanned(), user.isEnabled(), user.getRoles());
    }
}
