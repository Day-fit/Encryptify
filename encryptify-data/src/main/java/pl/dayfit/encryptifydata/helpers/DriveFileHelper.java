package pl.dayfit.encryptifydata.helpers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.dayfit.encryptifydata.repositories.DriveFileRepository;

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
