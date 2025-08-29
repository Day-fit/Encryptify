package pl.dayfit.encryptifycore.service;

import io.minio.errors.InsufficientDataException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifycore.cacheservice.DriveFolderCacheService;
import pl.dayfit.encryptifycore.dto.FileSystemDto;
import pl.dayfit.encryptifycore.dto.FolderCreateDto;
import pl.dayfit.encryptifycore.dto.FolderRenameDto;
import pl.dayfit.encryptifycore.entity.DriveFile;
import pl.dayfit.encryptifycore.entity.DriveFolder;
import pl.dayfit.encryptifycore.exception.FileActionException;
import pl.dayfit.encryptifycore.mapper.FileResponseMapper;
import pl.dayfit.encryptifycore.mapper.FolderResponseDtoMapper;
import pl.dayfit.encryptifycore.repository.DriveFolderRepository;

import javax.annotation.Nullable;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FolderManagementService {
    private final DriveFolderRepository repository;
    private final DriveFolderCacheService driveFolderCacheService;
    private final FileResponseMapper fileResponseMapper;
    private final FolderResponseDtoMapper folderResponseDtoMapper;
    private final MinioService minioService;

    /**
     * Handles logic of folder creation
     * @param dto DTO with folder's name and folder's parent
     * @param username folder's owner username
     * @return created folder entity
     * @throws DuplicateKeyException when folder with same parent and same name already exists
     */
    public DriveFolder createFolder(FolderCreateDto dto, String username) {
        String name = dto.folderName();
        DriveFolder folder = new DriveFolder();

        if (repository.existInSameParentAndName(dto.parentId(), name))
        {
            throw new DuplicateKeyException("Folder with name " + name + " already exists");
        }

        folder.setName(name);
        folder.setUploader(username);
        folder.setUuid(UUID.randomUUID());
        folder.setCreationDate(Instant.now());

        if (dto.parentId() != null) {

            DriveFolder parent = driveFolderCacheService.getDriveDirectoryById(dto.parentId());

            parent.getChildren().add(folder);
            folder.setParent(parent);

            folder.setPath(parent.getPath() + "/" + name + "/");
        } else {
            folder.setPath(name + "/");
        }

        return driveFolderCacheService.save(folder);
    }

    /**
     * Handles logic of a folder deletion
     * @param folderId id of the folder to delete
     * @param username username used to check if user is folder's owner
     */
    public void deleteFolder(long folderId, String username) {

        try {
            DriveFolder folder = driveFolderCacheService.getDriveDirectoryById(folderId);
            List<String> paths = new ArrayList<>();

            List<DriveFolder> toSearch = new ArrayList<>();
            toSearch.add(folder);

            while (!toSearch.isEmpty())
            {
               DriveFolder subFolder = toSearch.remove(0);
               toSearch.addAll(subFolder.getChildren());

               paths.addAll(subFolder.getFiles()
                       .stream()
                       .map(DriveFile::getPath)
                       .toList());
            }

            minioService.deleteFiles(username,
                    paths.toArray(String[]::new)
            );

            driveFolderCacheService.deleteDriveDirectory(folderId);
        } catch (IOException e) {
            log.error("IOException when deleting folder", e);
            throw new FileActionException("Failed to delete files in folder. Folder wasn't deleted.");
        } catch (InsufficientDataException ex) {
            log.warn("Not enough memory to complete operation.", ex);
            throw new FileActionException("Not enough memory to complete operation");
        }
    }

    /**
     * Handles logic of renaming a folder
     * @param renameDto DTO that contains folder id and it's new name
     * @param username username used to check if user is folder's owner
     */
    public void renameFolder(FolderRenameDto renameDto, String username) {
        DriveFolder folder = driveFolderCacheService
                .getDriveDirectoryById(renameDto.id());

        if(!folder.getUploader().equals(username))
        {
            throw new AccessDeniedException("You are not allowed to rename this folder");
        }

        String[] pathFragments = folder.getPath()
                .split("/");

        pathFragments[Math.max(0, pathFragments.length - 1)] = folder.getName();
        String newPath = String.join("/", pathFragments) + "/";
        folder.setPath(newPath);
        folder.setName(renameDto.newName());

        minioService.renameFolder(newPath);
        driveFolderCacheService.save(folder);
    }

    /**
     * Finds files in given folder
     * @param name uploader username (used if folderId is null)
     * @param folderId id of folder to search files in
     * @return DTO List of files in given folder
     */
    public List<FileSystemDto> getContent(String name, @Nullable Long folderId) {
        List<FileSystemDto> result = new ArrayList<>();

        if (folderId != null) {
            DriveFolder folder = driveFolderCacheService.getDriveDirectoryById(folderId);

            if (!folder.getUploader().equals(name)) {
                throw new AccessDeniedException("You are not owner of this folder");
            }

            result.addAll(
                    driveFolderCacheService
                            .getFiles(folderId)
                            .stream()
                            .map(fileResponseMapper::toDto)
                            .toList()
            );

            result.addAll(
                    driveFolderCacheService
                            .getFolders(folderId)
                            .stream()
                            .map(folderResponseDtoMapper::toDto)
                            .toList()
            );

            return result;
        }

        result.addAll(
                driveFolderCacheService
                        .getParentlessFiles(name)
                        .stream()
                        .map(fileResponseMapper::toDto)
                        .toList()
        );

        result.addAll(
                driveFolderCacheService
                        .getParentlessFolders(name)
                        .stream()
                        .map(folderResponseDtoMapper::toDto)
                        .toList()
        );

        return result;
    }
}
