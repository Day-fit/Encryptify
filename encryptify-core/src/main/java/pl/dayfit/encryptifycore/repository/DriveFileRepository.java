package pl.dayfit.encryptifycore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.dayfit.encryptifycore.entity.DriveFile;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriveFileRepository extends JpaRepository<DriveFile, Long> {
    @Query("SELECT d.id FROM DriveFile d WHERE d.parent.id = :id")
    List<Long> findAllIdByParent_Id(Long id);

    @Query("SELECT d.uploader FROM DriveFile d WHERE d.id = :id")
    Optional<String> findUploader_UsernameById(Long id);

    List<DriveFile> findAllByParent_Id(Long id);

    List<DriveFile> findAllByParentNullAndUploader(String uploader);
}