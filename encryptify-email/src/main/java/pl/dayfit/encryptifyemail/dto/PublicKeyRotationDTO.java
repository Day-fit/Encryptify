package pl.dayfit.encryptifyemail.dto;

public record PublicKeyRotationDTO(byte[] encodedKey, int keyId) {
}
