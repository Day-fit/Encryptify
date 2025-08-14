package pl.dayfit.encryptifyauth.accountcheck;

import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@Profile("!no-email")
public class AccountActivatedCheck implements AccountCheck {
    @Override
    public void check(UserDetails userDetails) {
        if (userDetails.isEnabled())
        {
            return;
        }

        throw new DisabledException("Account is disabled");
    }
}
