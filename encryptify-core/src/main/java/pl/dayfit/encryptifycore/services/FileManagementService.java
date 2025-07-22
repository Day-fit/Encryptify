package pl.dayfit.encryptifycore.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifycore.dto.FileRequestDto;
import pl.dayfit.encryptifycore.mappers.FileUploadDtoMapper;

@Service
@RequiredArgsConstructor
public class FileManagementService {
    private final FileUploadDtoMapper fileUploadDtoMapper;

    public void handleFileUpload(FileRequestDto fileRequestDto, String uploadedBy)
    {
        fileUploadDtoMapper.toDestination(fileRequestDto, uploadedBy);
    }
}
