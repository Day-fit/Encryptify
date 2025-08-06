package pl.dayfit.encryptifydata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dayfit.encryptifydata.entity.DriveDirectory;

@Repository
public interface DriveDirectoryRepository extends JpaRepository<DriveDirectory, Long> {
}