package pl.dayfit.encryptifyauth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncryptifyUser {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String emailProof;

    @Column(nullable = false)
    private String emailHashLookup;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Instant registrationDate;

    private boolean isBanned = false;
    private boolean isEnabled = false;

    @Column(nullable = false)
    private List<String> roles;

    private Instant emailVerificationDate;

    @Column(nullable = false, unique = true)
    private String bucketName;
}
