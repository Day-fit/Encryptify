package pl.dayfit.encryptifycore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dayfit.encryptifycore.entity.DriveDirectory;

@Repository
public interface DriveDirectoryRepository extends JpaRepository<DriveDirectory, Long> {
}