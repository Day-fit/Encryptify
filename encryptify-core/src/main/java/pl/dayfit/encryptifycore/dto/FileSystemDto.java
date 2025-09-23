package pl.dayfit.encryptifycore.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.dayfit.encryptifycore.type.FileSystemTypes;

public interface FileSystemDto {
    Long id();
    @JsonProperty("type")
    FileSystemTypes type();
    String name();
}
