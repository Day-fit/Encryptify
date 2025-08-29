package pl.dayfit.encryptifycore.dto;

import pl.dayfit.encryptifycore.types.FileSystemTypes;

import java.time.Instant;

public record FolderResponseDto(Long id, String name, Instant creationDate) implements FileSystemDto {
    @Override
    public FileSystemTypes type() {
        return FileSystemTypes.FOLDER;
    }
}
