package pl.dayfit.encryptifycore.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.dayfit.encryptifycore.repository.DriveFileRepository;

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
