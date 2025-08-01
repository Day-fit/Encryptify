package pl.dayfit.encryptifydata.helpers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.dayfit.encryptifydata.entities.DriveFile;
import pl.dayfit.encryptifydata.repositories.DriveFileRepository;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class DriveFileAccessHelper {
    private final DriveFileRepository driveFileRepository;

    public boolean isOwner(DriveFile driveFile, String username)
    {
        return driveFile.getUploader().getUsername().equals(username);
    }

    public boolean isOwner(long id, String username)
    {
        return driveFileRepository.findUploader_UsernameById(id)
                .orElseThrow(() -> new NoSuchElementException("There is no file with ID " + id))
                .equals(username);
    }
}
