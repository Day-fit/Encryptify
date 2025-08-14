package pl.dayfit.encryptifycore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifycore.dto.FileRequestDto;
import pl.dayfit.encryptifycore.mapper.FileUploadDtoMapper;
import pl.dayfit.encryptifycore.cacheservice.DriveFileCacheService;
import pl.dayfit.encryptifycore.entity.DriveFile;
import pl.dayfit.encryptifycore.helper.DriveFileAccessHelper;

@Service
@RequiredArgsConstructor
public class FileManagementService {
    private final DriveFileAccessHelper accessHelper;
    private final DriveFileCacheService driveFileCacheService;
    private final FileUploadDtoMapper fileUploadDtoMapper;

    public void handleFileUpload(FileRequestDto fileRequestDto, String uploader)
    {
        DriveFile file = fileUploadDtoMapper.toDestination(
                fileRequestDto,
                uploader
        );

        driveFileCacheService.save(file);
    }

    public void handleFileDeletion(Long id, String uploader) {
        if (!accessHelper.isOwner(id,  uploader))
        {
            throw new AccessDeniedException("You are not owner of this file");
        }

        driveFileCacheService.deleteDriveFile(id);
    }
}
