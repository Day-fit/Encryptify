package pl.dayfit.encryptifycore.types;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FileSystemTypes {
    FOLDER("FOLDER"),
    FILE("FILE");

    private final String label;

    @Override
    public String toString() {
        return label;
    }
}
