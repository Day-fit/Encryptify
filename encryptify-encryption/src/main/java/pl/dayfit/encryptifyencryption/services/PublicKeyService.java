package pl.dayfit.encryptifyencryption.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifydata.cacheservices.DriveDirectoryCacheService;
import pl.dayfit.encryptifydata.cacheservices.DriveFileCacheService;
import pl.dayfit.encryptifydata.entities.DriveDirectory;
import pl.dayfit.encryptifydata.entities.DriveFile;
import pl.dayfit.encryptifyencryption.dto.PublicKeyUploadDto;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class PublicKeyService {
    private final DriveDirectoryCacheService driveDirectoryCacheService;
    private final DriveFileCacheService driveFileCacheService;

    public void assignPublicKey(PublicKeyUploadDto publicKeyUploadDto) {
        Consumer<PublicKeyUploadDto> resolver = publicKeyUploadDto.isDirectory() ? this::assignPublicKeyToDirectory : this::assignPublicKeyToFile;
        resolver.accept(publicKeyUploadDto);
    }

    private void assignPublicKeyToDirectory(PublicKeyUploadDto publicKeyDto) {
        DriveDirectory directory = driveDirectoryCacheService.getDriveDirectoryById(publicKeyDto.id());

        directory.getFiles().forEach(file ->
        {
            file.setPublicKey(publicKeyDto.publicKey());
            driveFileCacheService.save(file);
        });
    }

    private void assignPublicKeyToFile(PublicKeyUploadDto publicKeyDto) {
        DriveFile driveFile = driveFileCacheService.getDriveFileById(publicKeyDto.id());
        driveFile.setPublicKey(publicKeyDto.publicKey());
        driveFileCacheService.save(driveFile);
    }
}
