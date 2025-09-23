package pl.dayfit.encryptify.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dayfit.encryptify.stats.entity.UserStatistics;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserStatisticsRepository extends JpaRepository<UserStatistics, Long> {

    Optional<UserStatistics> findByUserId(UUID userId);
}
