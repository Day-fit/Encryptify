package pl.dayfit.encryptifyauth.authenticationprovider;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.dayfit.encryptifyauth.principal.UserPrincipal;
import pl.dayfit.encryptifyauth.service.UserDetailsService;
import pl.dayfit.encryptifyauth.token.UserDetailsToken;
import pl.dayfit.encryptifyauth.token.UserDetailsTokenCandidate;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class UserDetailsAuthenticationProvider implements AuthenticationProvider {
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails user = userDetailsService.loadUserByUsername(authentication.getName());
        Principal principal = new UserPrincipal(user);

        if(!passwordEncoder.matches((String) authentication.getCredentials(), user.getPassword()))
        {
            throw new BadCredentialsException("Invalid username or password");
        }

        return new UserDetailsToken(user.getAuthorities(), principal);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UserDetailsTokenCandidate.class.isAssignableFrom(authentication);
    }
}
