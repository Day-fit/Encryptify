package pl.dayfit.encryptifydata.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dayfit.encryptifydata.entities.DriveDirectory;

@Repository
public interface DriveDirectoryRepository extends JpaRepository<DriveDirectory, Long> {
}