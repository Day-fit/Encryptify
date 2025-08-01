package pl.dayfit.encryptifydata.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
public class DriveFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID uuid;

    @Column(nullable = false)
    private String name;

    @Lob
    @Column(nullable = false)
    private byte[] content;

    @Column(nullable = false)
    private Instant uploadDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private EncryptifyUser uploader;

    @ManyToOne(fetch = FetchType.LAZY)
    private DriveDirectory parent;

    private String publicKey;
}
