package pl.dayfit.encryptifycore.cacheservice;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifycore.entity.DriveDirectory;
import pl.dayfit.encryptifycore.helper.DriveFileHelper;
import pl.dayfit.encryptifycore.repository.DriveDirectoryRepository;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class DriveDirectoryCacheService {
    private final CacheManager cacheManager;
    private final DriveFileHelper driveFileHelper;
    private final DriveDirectoryRepository driveDirectoryRepository;

    @Cacheable(key = "#id", value = "directory")
    public DriveDirectory getDriveDirectoryById(Long id) {
        return driveDirectoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No directory found with id: " + id));
    }

    @CachePut(key = "#driveDirectory.id", value = "directory")
    public DriveDirectory save(DriveDirectory driveDirectory) {
        return driveDirectoryRepository.save(driveDirectory);
    }

    @Caching(evict = {
            @CacheEvict(key = "#id", value = "directory"),
            @CacheEvict(key = "#id", value = "directory.files")
    })
    public void deleteDriveDirectory(Long id) {
        Cache cache = cacheManager.getCache("files");
        driveDirectoryRepository.deleteById(id);

        if (cache == null) {
            return;
        }

        driveFileHelper.getDependentFilesId(id).forEach(cache::evict);
    }
}
