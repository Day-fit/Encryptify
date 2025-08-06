package pl.dayfit.encryptifyauthlib.service;

import io.jsonwebtoken.security.Keys;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifyauthlib.dto.PublicKeyRotationDTO;

import java.security.Key;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@ConditionalOnBooleanProperty(value = "key-listener.enabled")
@SuppressWarnings("unused")
public class JwtRotationListener {
    private final AtomicInteger currentIndex = new AtomicInteger(0);
    private final ConcurrentHashMap<Integer, Key> secretKeys = new ConcurrentHashMap<>();

    @RabbitListener(queues = "${service.name}")
    public void handleKeyRotation(PublicKeyRotationDTO keyRotationDTO) {
         byte[] decodedKeyBytes = Base64.getDecoder().decode(keyRotationDTO.encodedKey());
         currentIndex.set(keyRotationDTO.keyId());
         secretKeys.put(keyRotationDTO.keyId(), Keys.hmacShaKeyFor(decodedKeyBytes));
    }

    public Key getSecretKey(int keyId)
    {
        return secretKeys.get(keyId);
    }

    public Integer getCurrentIndex()
    {
        return currentIndex.get();
    }
}
