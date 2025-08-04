package pl.dayfit.encryptifyauthlib.dto;

public record PublicKeyRotationDTO(byte[] encodedKey, int keyId) {
}
