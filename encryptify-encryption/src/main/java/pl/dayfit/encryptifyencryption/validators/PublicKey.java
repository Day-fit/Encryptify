package pl.dayfit.encryptifyencryption.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PublicKeyValidator.class)
@Documented
public @interface PublicKey {
    String message() default "Public key is invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
