package pl.dayfit.encryptifydata.cacheservices;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifydata.entities.DriveFile;
import pl.dayfit.encryptifydata.repositories.DriveFileRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class DriveFileCacheService {
    private final DriveFileRepository driveFileRepository;

    @Cacheable(key = "#id", value = "files")
    public DriveFile getDriveFileById(Long id)
    {
        return driveFileRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("File not found"));
    }

    @CachePut(key = "#driveFile.id", value = "files")
    public DriveFile save(DriveFile driveFile)
    {
        return driveFileRepository.save(driveFile);
    }

    @CacheEvict(key = "#id", value = "files")
    public void deleteDriveFile(Long id)
    {
        driveFileRepository.deleteById(id);
    }

    @Cacheable(key = "#id", value = "directory.files")
    public List<DriveFile> getFilesByDirectoryId(Long id)
    {
        return driveFileRepository.findAllByParent_Id(id);
    }
}
