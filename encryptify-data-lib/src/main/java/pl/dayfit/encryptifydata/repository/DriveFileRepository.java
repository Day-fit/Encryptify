package pl.dayfit.encryptifydata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.dayfit.encryptifydata.entity.DriveFile;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriveFileRepository extends JpaRepository<DriveFile, Long> {
    List<DriveFile> findAllByParent_Id(Long id);

    @Query("SELECT d.id FROM DriveFile d WHERE d.parent.id = :id")
    List<Long> findAllIdByParent_Id(Long id);

    @Query("SELECT d.uploader.username FROM DriveFile d WHERE d.id = :id")
    Optional<String> findUploader_UsernameById(Long id);
}
