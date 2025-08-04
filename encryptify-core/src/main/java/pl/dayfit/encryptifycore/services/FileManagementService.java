package pl.dayfit.encryptifycore.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifycore.dto.FileRequestDto;
import pl.dayfit.encryptifycore.mappers.FileUploadDtoMapper;
import pl.dayfit.encryptifydata.cacheservice.DriveFileCacheService;
import pl.dayfit.encryptifydata.entity.DriveFile;
import pl.dayfit.encryptifydata.helper.DriveFileAccessHelper;

@Service
@RequiredArgsConstructor
public class FileManagementService {
    private final DriveFileAccessHelper accessHelper;
    private final DriveFileCacheService driveFileCacheService;
    private final FileUploadDtoMapper fileUploadDtoMapper;

    public void handleFileUpload(FileRequestDto fileRequestDto, String uploadedBy)
    {
        DriveFile file = fileUploadDtoMapper.toDestination(fileRequestDto, uploadedBy);
        driveFileCacheService.save(file);
    }

    public void handleFileDeletion(Long id, String uploadedBy) {
        if (!accessHelper.isOwner(id,  uploadedBy))
        {
            throw new AccessDeniedException("You are not owner of this file");
        }

        driveFileCacheService.deleteDriveFile(id);
    }
}
