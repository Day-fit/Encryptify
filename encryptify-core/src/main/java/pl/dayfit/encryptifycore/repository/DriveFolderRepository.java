package pl.dayfit.encryptifycore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.dayfit.encryptifycore.entity.DriveFolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DriveFolderRepository extends JpaRepository<DriveFolder, Long> {
    @Query("SELECT f FROM DriveFolder f " +
            "WHERE (:parentId IS NULL AND f.parent.id IS NULL AND f.name = :folderName AND f.uploaderId = :uploaderId)" +
            "OR (f.parent.id = :parentId AND f.name = :folderName AND f.uploaderId = :uploaderId)")
    Optional<DriveFolder> findInSameParentAndNameAndOwner(Long parentId, String folderName, UUID uploaderId);

    List<DriveFolder> findAllByParentNullAndUploaderId(UUID uploaderId);
}