package pl.dayfit.encryptifyauth.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifyauth.configuration.CookieConfigurationProperties;
import pl.dayfit.encryptifyauth.event.JwtKeyRotatedEvent;

import java.security.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtSecretRotationService {
    private final CookieConfigurationProperties cookieConfigurationProperties;
    private final AtomicInteger currentSecretKey = new AtomicInteger(-1);
    private final ApplicationEventPublisher applicationEventPublisher;
    private int MAX_SECRET_KEYS_NUMBER;

    private PrivateKey privateKey;
    private final Map<Integer, PublicKey> publicKeys = new ConcurrentHashMap<>();

    @PostConstruct
    private void init()
    {
        MAX_SECRET_KEYS_NUMBER = cookieConfigurationProperties.getRefreshTokenValidityDays() + 1;
    }

    public synchronized void generateNewSecretKey()
    {
        PublicKey publicKey;
        int index = (currentSecretKey.get() + 1) % MAX_SECRET_KEYS_NUMBER;

        try {
            KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("Ed25519");
            KeyPair keyPair = keyGenerator.generateKeyPair();

            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        currentSecretKey.set(index);
        publicKeys.put(index, publicKey);
        applicationEventPublisher.publishEvent(new JwtKeyRotatedEvent(publicKey, index));

        log.info("Generated new secret key, new index: {}", index);
    }

    public PrivateKey getCurrentPrivateKey()
    {
        return privateKey;
    }

    public int getCurrentIndex()
    {
        return currentSecretKey.get();
    }

    public PublicKey getPublicKey(int index)
    {
        return publicKeys.get(index);
    }
}
