package pl.dayfit.encryptifycore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.dayfit.encryptifycore.entity.DriveFolder;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriveFolderRepository extends JpaRepository<DriveFolder, Long> {
    @Query("SELECT f FROM DriveFolder f " +
            "WHERE (:parentId IS NULL AND f.parent.id IS NULL AND f.name = :folderName AND f.uploader = :owner)" +
            "OR (f.parent.id = :parentId AND f.name = :folderName AND f.uploader = :owner)")
    Optional<DriveFolder> findInSameParentAndNameAndOwner(Long parentId, String folderName, String owner);

    List<DriveFolder> findAllByParentNullAndUploader(String uploader);
}