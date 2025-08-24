package pl.dayfit.encryptifyencryption.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dayfit.encryptifyencryption.entity.DriveDirectory;

@Repository
public interface DriveDirectoryRepository extends JpaRepository<DriveDirectory, Long> {
}