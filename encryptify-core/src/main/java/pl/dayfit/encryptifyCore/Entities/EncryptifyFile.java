package pl.dayfit.encryptifyCore.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
public class EncryptifyFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID uuid;

    private String fileName;
    private String filePath;
    private String data;

    private Instant uploadTime;

    @ManyToOne(fetch = FetchType.LAZY)
    private User uploader;
}
