package pl.dayfit.encryptifyemail.service;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifyemail.dto.PublicKeyRotationDTO;

import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class JwtRotationListener {
    private final AtomicInteger currentIndex = new AtomicInteger(0);
    private final ConcurrentHashMap<Integer, PublicKey> secretKeys = new ConcurrentHashMap<>();

    /**
     * Handles receiving public keys from `auth` microservice
     * @param keyRotationDTO dto that represent public key
     */
    @RabbitListener(queues = "service.email")
    private void handleKeyRotation(PublicKeyRotationDTO keyRotationDTO, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("Ed25519");
            byte[] decodedKeyBytes = Base64.getDecoder().decode(keyRotationDTO.encodedKey());
            KeySpec keySpec = new X509EncodedKeySpec(decodedKeyBytes);

            secretKeys.put(keyRotationDTO.keyId(), keyFactory.generatePublic(keySpec));
            currentIndex.set(keyRotationDTO.keyId());

            log.info("Key rotation success. New key index: {}", currentIndex.get());
        } catch (NoSuchAlgorithmException e) {
            log.error("Invalid algorithm was specified in a code.", e);
        } catch (Exception e) {
            log.warn("{}, NACKing message", e.getMessage());

            try {
                channel.basicNack(tag, false, true);
            } catch (IOException ioE) {
                throw new RuntimeException(ioE);
            }
        }
    }

    public Key getSecretKey(int keyId)
    {
        return secretKeys.get(keyId);
    }
}
