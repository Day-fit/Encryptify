package pl.dayfit.encryptifyauth.authenticationprovider;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.dayfit.encryptifyauth.accountcheck.AccountCheckChain;
import pl.dayfit.encryptifyauthlib.principal.UserDetailsImpl;
import pl.dayfit.encryptifyauthlib.principal.UserPrincipal;
import pl.dayfit.encryptifyauth.service.EncryptifyUserDetailsService;
import pl.dayfit.encryptifyauth.token.UserDetailsToken;
import pl.dayfit.encryptifyauth.token.UserDetailsTokenCandidate;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class UserDetailsAuthenticationProvider implements AuthenticationProvider {
    private final PasswordEncoder passwordEncoder;
    private final EncryptifyUserDetailsService encryptifyUserDetailsService;
    private final AccountCheckChain accountCheckChain;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetailsImpl user = (UserDetailsImpl) encryptifyUserDetailsService.loadUserByUsername(authentication.getName());
        Principal principal = new UserPrincipal(user.getUsername());

        if(!passwordEncoder.matches((String) authentication.getCredentials(), user.getPassword()))
        {
            throw new BadCredentialsException("Account does not exist or password does not match");
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
        accountCheckChain
                .run(userDetails);
    }
}
