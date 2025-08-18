package pl.dayfit.encryptifyauth.event;

public record UserRegisteredEvent(String username, String receiverEmail, String verificationToken) {
}
