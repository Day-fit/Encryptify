package pl.dayfit.encryptifycore.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class DriveFolder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    private DriveFolder parent;

    @JsonManagedReference
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<DriveFolder> children = new ArrayList<>();

    @Column(nullable = false)
    private UUID uuid;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private UUID uploaderId;

    @Column(nullable = false)
    private Instant creationDate;

    @JsonManagedReference
    @OneToMany(fetch =  FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "parent")
    private List<DriveFile> files = new ArrayList<>();

    @Column(nullable = false)
    private String path;
}
