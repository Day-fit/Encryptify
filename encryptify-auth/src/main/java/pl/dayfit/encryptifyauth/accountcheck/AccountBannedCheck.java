package pl.dayfit.encryptifyauth.accountcheck;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Component;

@Component
public class AccountBannedCheck implements AccountCheck {

    /**
     * Checks if user account is blocked
     * @param userDetails details of the account
     * @throws LockedException if account is blocked
     */
    @Override
    public void check(UserDetails userDetails) {
        if (userDetails.isAccountNonLocked())
        {
            return;
        }

        throw new LockedException("Sorry, your account is locked.");
    }
}
