package pl.dayfit.encryptify.stats.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.dayfit.encryptify.stats.type.ActivityType;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @JoinColumn(unique = true)
    private UUID userId;

    private Long totalFileCount = 0L;
    private Long totalFolderCount = 0L;
    private Long totalStorageUsed = 0L;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private RecentActivity recentActivity;
}
