package pl.dayfit.encryptifycore.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.dayfit.encryptifycore.entity.DriveFile;
import pl.dayfit.encryptifycore.repository.DriveFileRepository;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class DriveFileAccessHelper {
    private final DriveFileRepository driveFileRepository;

    @SuppressWarnings("unused")
    public boolean isOwner(DriveFile driveFile, String uploader)
    {
        return driveFile.getUploader().equals(uploader);
    }

    public boolean isOwner(long id, String username)
    {
        return driveFileRepository.findUploader_UsernameById(id)
                .orElseThrow(() -> new NoSuchElementException("There is no file with ID " + id))
                .equals(username);
    }
}
