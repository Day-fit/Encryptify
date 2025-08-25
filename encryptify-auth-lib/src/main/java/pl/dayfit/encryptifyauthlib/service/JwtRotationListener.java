package pl.dayfit.encryptifyauthlib.service;

import com.nimbusds.jose.jwk.JWKSet;
import com.rabbitmq.client.Channel;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import pl.dayfit.encryptifyauthlib.configuration.JWKSConfigurationProperties;
import pl.dayfit.encryptifyauthlib.dto.JwksRotationEvent;

import java.io.IOException;
import java.io.SyncFailedException;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnBooleanProperty(value = "key-listener.enabled")
@EnableConfigurationProperties(JWKSConfigurationProperties.class)
@SuppressWarnings("unused")
public class JwtRotationListener {
    private final JWKSConfigurationProperties jwksConfigurationProperties;
    private final JwtClaimsService jwtClaimsService;

    @Getter
    private JWKSet jwks;

    @PostConstruct
    private void init()
    {
        try {
            jwtClaimsService.setJwkSetSupplier(this::getJwks);
            updateJwks();
            log.info("JWKS synchronization went successful");
        } catch (ResourceAccessException ex) {
            log.warn("JWKS synchronization went failed, given uri is unreachable", ex);
        } catch (SyncFailedException ex) {
            log.warn("Could not update JWKS", ex);
        } catch (ParseException ex) {
            log.warn("Could not parse JWKS", ex);
        }
    }

    /**
     * Handles receiving public keys from `auth` microservice
     * @param keyRotationEvent dto that represent public key
     */
    @RabbitListener(queues = "service.${service.name}")
    private void handleKeyRotation(JwksRotationEvent keyRotationEvent, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            updateJwks();
            Duration delay = Duration.between(keyRotationEvent.issuedAt(), Instant.now());

            log.info("Key rotation success. Delay {} ms", delay.toMillis());
        } catch (Exception e) {
            log.warn("NACKing message", e);

            try {
                channel.basicNack(tag, false, true);
            } catch (IOException ioE) {
                throw new RuntimeException(ioE);
            }
        }
    }

    /**
     * Fetches current JWK Set of public keys, handles message NACK management
     * @throws SyncFailedException when method cannot handle JWKS sync (response != 2XX)
     * @throws ResourceAccessException when given uri is unreachable
     * @throws ParseException when JWKSet parse fails
     */
    private void updateJwks() throws SyncFailedException, ParseException, ResourceAccessException
    {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(
                jwksConfigurationProperties.getJwksUri(),
                String.class
        );

        String body = response.getBody();
        if (!response.getStatusCode().is2xxSuccessful() || body == null || body.isEmpty())
        {
            throw new SyncFailedException("JWKS could not be updated");
        }

        this.jwks = JWKSet.parse(body);
    }
}
