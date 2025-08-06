package pl.dayfit.encryptifydata.cacheservice;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifydata.entity.EncryptifyUser;
import pl.dayfit.encryptifydata.repository.UserRepository;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserCacheService {
    private final UserRepository userRepository;

    @Cacheable(key = "#identifier", value = "user")
    public EncryptifyUser getUserByIdentifier(String identifier)
    {
        return userRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }

    @Cacheable(key = "#id", value = "user.id")
    public EncryptifyUser getUserById(long id)
    {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    @Cacheable(key = "#identifier", value = "user.identifier")
    public long getUserIdByIdentifier(String identifier)
    {
        return userRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new NoSuchElementException("Identifier not found"))
                .getId();
    }

    @Caching(
            @CachePut(key = "#identifier", value = "user.identifier")
    )
    public EncryptifyUser saveUser(EncryptifyUser user)
    {
        return userRepository.save(user);
    }
}
