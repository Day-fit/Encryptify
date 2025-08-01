package pl.dayfit.encryptifydata.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class DriveDirectory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private DriveDirectory parent;

    @Column(nullable = false)
    private UUID uuid;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private EncryptifyUser uploadedBy;

    @OneToMany(fetch =  FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "parent")
    private List<DriveFile> files;
}
