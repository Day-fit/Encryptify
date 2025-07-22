package pl.dayfit.encryptifydata.cacheservices;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifydata.entities.User;
import pl.dayfit.encryptifydata.repositories.UserRepository;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserCacheService {
    private final UserRepository userRepository;

    @Cacheable(key = "#username", value = "users")
    public User getUserByUsername(String username)
    {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Username not found"));
    }
}
