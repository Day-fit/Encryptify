package pl.dayfit.encryptifyencryption.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.dayfit.encryptifyencryption.repository.DriveFileRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class DriveFileHelper {
    private final DriveFileRepository driveFileRepository;

    public List<Long> getDependentFilesId(Long id)
    {
        return driveFileRepository.findAllIdByParent_Id(id);
    }
}
