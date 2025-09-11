package pl.dayfit.encryptifycore.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @Column(nullable = false)
    private Instant uploadDate;

    @Column(nullable = false)
    private String uploader;

    @Column(nullable = false)
    private String fileSize;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    private DriveFolder parent;

    @Column(nullable = false)
    private String path;

    private String publicKey;
}
