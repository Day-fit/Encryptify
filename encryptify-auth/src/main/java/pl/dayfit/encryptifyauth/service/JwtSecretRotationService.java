package pl.dayfit.encryptifyauth.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.gen.OctetKeyPairGenerator;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifyauth.event.JwtKeyRotatedEvent;
import pl.dayfit.encryptifyauth.configuration.JwtConfigurationProperties;
import pl.dayfit.encryptifyauthlib.service.JwtClaimsService;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtSecretRotationService {
    private final JwtConfigurationProperties jwtConfigurationProperties;
    private final JwtClaimsService jwtClaimsService;
    private final AtomicInteger currentSecretKey = new AtomicInteger(-1);
    private final ApplicationEventPublisher applicationEventPublisher;
    private int MAX_SECRET_KEYS_NUMBER;

    @Getter
    private OctetKeyPair currentOctetKeyPair;
    @Getter
    private final List<JWK> publicKeys = new ArrayList<>();

    @PostConstruct
    private void init()
    {
        MAX_SECRET_KEYS_NUMBER = jwtConfigurationProperties.getRefreshTokenValidityDays() + 1;
        jwtClaimsService.setJwkSetSupplier(this::getPublicKeysAsJWKSet);
    }

    public synchronized void generateNewSecretKey() throws Exception{
        int index = (currentSecretKey.get() + 1) % MAX_SECRET_KEYS_NUMBER;

        try
        {
            currentOctetKeyPair = new OctetKeyPairGenerator(Curve.Ed25519)
                    .keyUse(KeyUse.SIGNATURE)
                    .keyID(String.valueOf(index))
                    .issueTime(new Date())
                    .generate();

            JWK publicKey = currentOctetKeyPair.toPublicJWK();

            currentSecretKey.set(index);

            Callable<?> updateHandler = publicKeys.size() > index
                    ? () -> publicKeys.set(index, publicKey)
                    : () -> publicKeys.add(publicKey);

            updateHandler.call();
            applicationEventPublisher.publishEvent(new JwtKeyRotatedEvent());

            log.info("Generated new secret key, new index: {}", index);
        } catch (JOSEException ex) {
            log.error("Could not generate new secret key.", ex);
        }
    }

    public int getCurrentIndex()
    {
        return currentSecretKey.get();
    }

    public JWKSet getPublicKeysAsJWKSet()
    {
        return new JWKSet(publicKeys);
    }
}
