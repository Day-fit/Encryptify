package pl.dayfit.encryptifyauth.cacheservice;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifyauth.entity.EncryptifyUser;
import pl.dayfit.encryptifyauth.repository.UserRepository;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Service that gives cached database results
 */

@Service
@RequiredArgsConstructor
public class EncryptifyUserCacheService {
    private final UserRepository userRepository;
    private final CacheManager cacheManager;

    @Cacheable(key = "#username", value = "user.username")
    public EncryptifyUser getUserByUsername(String username)
    {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Account does not exist or password does not match"));
    }

    /**
     * Finds a user with raw form of an email
     * @param lookup Lookup that will help identify account
     * @return List of accounts with same lookup
     * @throws UsernameNotFoundException when user is not found
     */
    @Cacheable(key = "#lookup", value = "user.lookup")
    public List<EncryptifyUser> getUserByEmailLookup(String lookup) //NOTE: Caching is safe because we are using only lookup (not a raw form)
    {
        return userRepository.findAllByEmailHashLookup(lookup)
                .orElseThrow(() -> new UsernameNotFoundException("Account does not exist or password does not match"));
    }

    @Caching(
            put = {
                    @CachePut(key = "#result.username", value = "user.username"),
                    @CachePut(key = "#result.emailHashLookup", value = "user.lookup"),
                    @CachePut(key = "#result.id", value = "user.id")
            }
    )
    public EncryptifyUser saveUser(EncryptifyUser user)
    {
        return userRepository.save(user);
    }

    /**
     * Deletes user by id from database and cache
     * @param userId userId of user you want to remove
     */
    @Transactional
    public void deleteUserById(String userId)
    {
        EncryptifyUser user = userRepository.findByUsername(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        userRepository.deleteById(UUID.fromString(userId));

        Cache usernameCache = cacheManager.getCache("user.userId");
        Cache emailCache = cacheManager.getCache("user.lookup");
        Cache idCache = cacheManager.getCache("user.id");

        if (usernameCache != null)
        {
            usernameCache.evict(user.getUsername());
        }

        if (emailCache != null)
        {
            emailCache.evict(user.getEmailHashLookup());
        }

        if (idCache != null)
        {
            idCache.evict(user.getId());
        }
    }

    @Caching(
            evict = {
                    @CacheEvict(key = "#user.username", value = "user.username"),
                    @CacheEvict(key = "#user.emailHashLookup", value = "user.lookup"),
                    @CacheEvict(key = "#user.id", value = "user.id")
            }
    )
    @SuppressWarnings("unused")
    public void deleteUser(EncryptifyUser user)
    {
        userRepository.delete(user);
    }

    @CachePut(key = "#userId", value = "user.id")
    public EncryptifyUser getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User with id " + userId + " not found"));
    }
}
