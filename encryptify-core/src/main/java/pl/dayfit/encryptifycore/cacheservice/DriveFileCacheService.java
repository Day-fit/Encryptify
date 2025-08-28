package pl.dayfit.encryptifycore.cacheservice;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifycore.entity.DriveFile;
import pl.dayfit.encryptifycore.repository.DriveFileRepository;

import java.util.List;
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
                            @CachePut(key = "result.parent.id", value = "folder.files")
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

    public void deleteDriveFile(Long id)
    {
        DriveFile driveFile = driveFileRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("File not found"));

        Cache cache = cacheManager.getCache("files");
        Cache folderCache = cacheManager.getCache("folder.files");

        if(cache != null)
        {
            cache.evict(id);
        }

        if(folderCache != null)
        {
            folderCache.evict(driveFile.getParent()
                    .getId());
        }

        driveFileRepository.deleteById(id);
    }

    @Caching(evict =
            {
                    @CacheEvict(key = "#driveFile.id", value = "files"),
                    @CacheEvict(key = "#driveFile.parent.id", value = "folder.files"),
                    @CacheEvict(key = "#driveFile.uploader", value = "parentless.files")
            })
    public void deleteDriveFile(DriveFile driveFile)
    {
        driveFileRepository.delete(driveFile);
    }

    @Cacheable(key = "#folderId", value = "folder.files")
    public List<DriveFile> getFilesFromFolder(long folderId)
    {
        return driveFileRepository.findAllByParent_Id(folderId);
    }

    @Cacheable(key = "uploader", value = "parentless.files")
    public List<DriveFile> getParentlessFiles(String uploader) {
        return driveFileRepository.findAllByParentNullAndUploader(uploader);
    }
}
