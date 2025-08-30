package pl.dayfit.encryptifycore.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UUIDValidator.class)
public @interface UUID {
    String message() default "Given UUID is invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
