package pl.dayfit.encryptifyCore.Dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class FileUploadDto {
    private final String fileName;
    private final String fileSize;
    private final String fileType;
    private final String data;
}
