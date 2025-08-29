package pl.dayfit.encryptifycore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.dayfit.encryptifycore.entity.DriveFolder;

import java.util.List;

@Repository
public interface DriveFolderRepository extends JpaRepository<DriveFolder, Long> {
    @Query("SELECT COUNT(f) > 0 FROM DriveFolder f " +
            "WHERE (:parentId IS NULL AND f.parent.id IS NULL AND f.name = :folderName)" +
            "OR (f.parent.id = :parentId AND f.name = :folderName)")
    boolean existInSameParentAndName(Long parentId, String folderName);

    List<DriveFolder> findAllByParentNullAndUploader(String uploader);
}