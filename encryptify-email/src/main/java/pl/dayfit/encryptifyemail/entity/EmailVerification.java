package pl.dayfit.encryptifyemail.entity;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@Setter
@RedisHash(value="EmailVerification",timeToLive = 86400)
public class EmailVerification implements Serializable {
    @Id
    private String id;
    private String username;
    private int verificationCode;
    private boolean isActive;
}
