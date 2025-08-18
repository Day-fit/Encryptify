package pl.dayfit.encryptifyemail.event;

public record UserRegisteredEvent(String username, String receiverEmail, String verificationToken) {
}
