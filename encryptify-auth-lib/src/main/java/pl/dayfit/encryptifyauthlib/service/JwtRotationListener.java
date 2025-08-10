package pl.dayfit.encryptifyauthlib.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifyauthlib.dto.PublicKeyRotationDTO;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@ConditionalOnBooleanProperty(value = "key-listener.enabled")
@SuppressWarnings("unused")
public class JwtRotationListener {
    private final AtomicInteger currentIndex = new AtomicInteger(0);
    private final ConcurrentHashMap<Integer, PublicKey> secretKeys = new ConcurrentHashMap<>();

    @RabbitListener(queues = "${service.name}")
    public void handleKeyRotation(PublicKeyRotationDTO keyRotationDTO) {
        try
        {
            KeyFactory keyFactory = KeyFactory.getInstance("Ed25519");
            byte[] decodedKeyBytes = Base64.getDecoder().decode(keyRotationDTO.encodedKey());
            KeySpec keySpec = new PKCS8EncodedKeySpec(decodedKeyBytes);

            secretKeys.put(keyRotationDTO.keyId(), keyFactory.generatePublic(keySpec));
            currentIndex.set(keyRotationDTO.keyId());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
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
