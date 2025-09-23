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
import pl.dayfit.encryptifycore.entity.DriveFolder;
import pl.dayfit.encryptifycore.helper.DriveFileHelper;
import pl.dayfit.encryptifycore.repository.DriveFolderRepository;
import pl.dayfit.encryptifycore.repository.DriveFileRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class DriveFolderCacheService {
    private final CacheManager cacheManager;
    private final DriveFileHelper driveFileHelper;
    private final DriveFolderRepository driveFolderRepository;
    private final DriveFileRepository driveFileRepository;

    /**
     * Searches for DriveFolder with given id
     * @param id id of folder we are searching for
     * @return instance of found DriveFolder
     * @throws NoSuchElementException if searching result is null
     */
    @Cacheable(key = "#id", value = "folder")
    public DriveFolder getDriveDirectoryById(Long id) throws NoSuchElementException {
        return driveFolderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No directory found with ID: " + id));
    }

    @Caching(evict =
            {
                    @CacheEvict(key = "#driveFolder.id", value = "folder.files"),
            }, put = {
            @CachePut(key = "#driveFolder.id", value = "folder")
    })
    public DriveFolder save(DriveFolder driveFolder) {
        return driveFolderRepository.save(driveFolder);
    }

    @Caching(evict = {
            @CacheEvict(key = "#id", value = "folder"),
            @CacheEvict(key = "#id", value = "folder.files"),
            @CacheEvict(key = "id", value = "folder.folders")
    })
    public void deleteDriveDirectory(Long id) {
        Cache cache = cacheManager.getCache("files");
        driveFolderRepository.deleteById(id);

        if (cache == null) {
            return;
        }

        driveFileHelper.getDependentFilesId(id).forEach(cache::evict);
    }

    @Caching(evict = {
            @CacheEvict(key = "#folder.id", value = "folder"),
            @CacheEvict(key = "#folder.id", value = "folder.files"),
            @CacheEvict(key = "#folder.id", value = "folder.folders")
    })
    public void deleteDriveDirectory(DriveFolder folder) {
        long id = folder.getId();

        Cache cache = cacheManager.getCache("files");
        driveFolderRepository.deleteById(id);

        if (cache == null) {
            return;
        }

        driveFileHelper.getDependentFilesId(id).forEach(cache::evict);
    }

    @Cacheable(key = "#folderId", value = "folder.files")
    public List<DriveFile> getFiles(long folderId)
    {
        return driveFileRepository.findAllByParent_Id(folderId);
    }

    @Cacheable(key = "#uploaderId", value = "parentless.files")
    public List<DriveFile> getParentlessFiles(UUID uploaderId) {
        return driveFileRepository.findAllByParentNullAndUploaderId(uploaderId);
    }

    @Cacheable(key = "#uploaderId", value = "parentless.folders")
    public List<DriveFolder> getParentlessFolders(UUID uploaderId) {
        return driveFolderRepository.findAllByParentNullAndUploaderId(uploaderId);
    }

    @Cacheable(key = "#folderId", value = "folder.folders")
    public List<DriveFolder> getFolders(Long folderId) {
        DriveFolder driveFolder = driveFolderRepository.findById(folderId)
                .orElseThrow(() -> new NoSuchElementException("No folder found with ID: " + folderId));

        return driveFolder.getChildren();
    }
}
