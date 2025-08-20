package pl.dayfit.encryptifyauth.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.jwk.gen.OctetKeyPairGenerator;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifyauth.event.JwtKeyRotatedEvent;
import pl.dayfit.encryptifyauthlib.configuration.JwtConfigurationProperties;

import java.security.*;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtSecretRotationService {
    private final JwtConfigurationProperties jwtConfigurationProperties;
    private final AtomicInteger currentSecretKey = new AtomicInteger(-1);
    private final ApplicationEventPublisher applicationEventPublisher;
    private int MAX_SECRET_KEYS_NUMBER;

    @Getter
    private OctetKeyPair currentOctetKeyPair;
    private final Map<Integer, OctetKeyPair> publicKeys = new ConcurrentHashMap<>();

    @PostConstruct
    private void init()
    {
        MAX_SECRET_KEYS_NUMBER = jwtConfigurationProperties.getRefreshTokenValidityDays() + 1;
    }

    public synchronized void generateNewSecretKey() {
        int index = (currentSecretKey.get() + 1) % MAX_SECRET_KEYS_NUMBER;

        try
        {
            currentOctetKeyPair = new OctetKeyPairGenerator(Curve.Ed25519)
                    .keyUse(KeyUse.SIGNATURE)
                    .keyID(String.valueOf(index))
                    .issueTime(new Date())
                    .generate();

            OctetKeyPair publicKey = currentOctetKeyPair.toPublicJWK();

            currentSecretKey.set(index);
            publicKeys.put(index, publicKey);
            applicationEventPublisher.publishEvent(new JwtKeyRotatedEvent(publicKey, index));

            log.info("Generated new secret key, new index: {}", index);
        } catch (JOSEException ex) {
            log.error("Could not generate new secret key.", ex);
        }
    }

    public int getCurrentIndex()
    {
        return currentSecretKey.get();
    }

    public OctetKeyPair getPublicKey(int index)
    {
        return publicKeys.get(index);
    }
}
