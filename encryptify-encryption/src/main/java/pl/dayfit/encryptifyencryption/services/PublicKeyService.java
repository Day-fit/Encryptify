package pl.dayfit.encryptifyencryption.services;

import org.springframework.stereotype.Service;
import pl.dayfit.encryptifyencryption.dto.PublicKeyUploadDto;

import java.util.function.Consumer;

@Service
public class PublicKeyService {
    public void assignPublicKey(PublicKeyUploadDto publicKeyUploadDto) {
        Consumer<PublicKeyUploadDto> resolver = publicKeyUploadDto.isDirectory() ? this::assignPublicKeyToDirectory : this::assignPublicKeyToFile;
        resolver.accept(publicKeyUploadDto);
    }

    private void assignPublicKeyToDirectory(PublicKeyUploadDto publicKeyDto) {

    }

    private void assignPublicKeyToFile(PublicKeyUploadDto publicKeyDto) {

    }
}
