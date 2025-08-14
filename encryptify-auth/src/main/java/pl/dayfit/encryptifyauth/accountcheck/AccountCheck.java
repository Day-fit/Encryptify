package pl.dayfit.encryptifyauth.accountcheck;

import org.springframework.security.core.userdetails.UserDetails;

public interface AccountCheck {
    void check(UserDetails userDetails);
}
