package pl.dayfit.encryptifycore.cacheservice;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifycore.entity.DriveFile;
import pl.dayfit.encryptifycore.entity.DriveFolder;
import pl.dayfit.encryptifycore.repository.DriveFileRepository;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DriveFileCacheService {
    private final DriveFileRepository driveFileRepository;
    private final CacheManager cacheManager;

    @Caching(
            put =
                    {
                            @CachePut(key = "#result.id", value = "files"),
                            @CachePut(key = "#result.parent.id", value = "folder.files")
                    }
    )
    public DriveFile getDriveFileById(Long id)
    {
        return driveFileRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("File not found"));
    }

    @Caching(put =
            {
                    @CachePut(key = "#driveFile.id", value = "files"),
                    @CachePut(key = "#driveFile.parent.id", value = "folder.files")
            })
    public void save(DriveFile driveFile)
    {
        DriveFile result = driveFileRepository.save(driveFile);

        if (result.getParent() != null)
        {
            return;
        }

        Cache cache = cacheManager.getCache("parentless.files");

        if (cache == null)
        {
            return;
        }

        cache.put(result.getUploader(), result);
    }

    @SuppressWarnings("unused")
    public void deleteDriveFile(Long id) //potentially useful in the future
    {
        DriveFile driveFile = driveFileRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("File not found"));

        Cache cache = cacheManager.getCache("files");
        Cache folderCache = cacheManager.getCache("folder.files");
        Cache parentCache = cacheManager.getCache("parentless.files");

        if (driveFile.getParent() == null && parentCache != null)
        {
            parentCache.evict(driveFile.getUploader());
        }

        if(cache != null)
        {
            cache.evict(id);
        }

        DriveFolder parent = driveFile.getParent();

        if(folderCache != null && parent != null)
        {
            folderCache.evict(parent.getId());
        }

        driveFileRepository.deleteById(id);
    }

    @Caching(evict =
            {
                    @CacheEvict(key = "#driveFile.id", value = "files"),
                    @CacheEvict(key = "#driveFile.parent.id", value = "folder.files"),
            })
    public void deleteDriveFile(DriveFile driveFile)
    {
        Cache parentCache = cacheManager.getCache("parentless.files");

        if (parentCache != null)
        {
            parentCache.evict(driveFile.getUploader());
        }

        driveFileRepository.delete(driveFile);
    }
}
