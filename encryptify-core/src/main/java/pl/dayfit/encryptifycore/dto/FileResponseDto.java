package pl.dayfit.encryptifycore.dto;

import pl.dayfit.encryptifycore.types.FileSystemTypes;

import java.time.Instant;

public record FileResponseDto (Long id, String name, String fileSize, Instant uploadDate) implements FileSystemDto {
    @Override
    public FileSystemTypes type() {
        return FileSystemTypes.FILE;
    }
}
