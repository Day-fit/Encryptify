package pl.dayfit.encryptifyauth.authenticationprovider;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.dayfit.encryptifyauth.principal.UserDetailsImpl;
import pl.dayfit.encryptifyauth.principal.UserPrincipal;
import pl.dayfit.encryptifyauth.service.EncryptifyUserDetailsService;
import pl.dayfit.encryptifyauth.token.UserDetailsToken;
import pl.dayfit.encryptifyauth.token.UserDetailsTokenCandidate;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class UserDetailsAuthenticationProvider implements AuthenticationProvider {
    private final PasswordEncoder passwordEncoder;
    private final EncryptifyUserDetailsService encryptifyUserDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetailsImpl user = (UserDetailsImpl) encryptifyUserDetailsService.loadUserByUsername(authentication.getName());
        Principal principal = new UserPrincipal(user);

        if(!passwordEncoder.matches((String) authentication.getCredentials(), user.getPassword()))
        {
            throw new BadCredentialsException("Invalid username or password");
        }

        additionalChecks(user);
        return new UserDetailsToken(user.getAuthorities(), principal, user.getUserId());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UserDetailsTokenCandidate.class.isAssignableFrom(authentication);
    }

    private void additionalChecks(UserDetails userDetails)
    {
        checkIfBanned(userDetails);
        checkIfDisabled(userDetails);
    }

    private void checkIfBanned(UserDetails details)
    {
        if (!details.isAccountNonLocked())
        {
            throw new LockedException("Account is banned");
        }
    }

    private void checkIfDisabled(UserDetails details)
    {
        if (!details.isEnabled())
        {
            throw new DisabledException("User is not enabled");
        }
    }
}
