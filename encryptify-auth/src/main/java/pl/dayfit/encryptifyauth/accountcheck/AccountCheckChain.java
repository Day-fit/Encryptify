package pl.dayfit.encryptifyauth.accountcheck;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountCheckChain {
    private final List<AccountCheck> accountChecks;

    public void run(UserDetails userDetails) {
        accountChecks
                .forEach(
                        check -> check.check(userDetails)
                );
    }
}
