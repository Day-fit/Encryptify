package pl.dayfit.encryptifyauth.dto;

import pl.dayfit.encryptifyauth.types.AccountType;

import java.time.Instant;
import java.util.List;

public record AccountInfoDto(
        String username,
        List<AccountType> accountType,
        Instant registrationDate
) {}
