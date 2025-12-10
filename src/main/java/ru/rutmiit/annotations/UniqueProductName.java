package ru.rutmiit.annotations;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.rutmiit.validators.UniqueProductNameValidator;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueProductNameValidator.class)
@Documented
public @interface UniqueProductName {
    String message() default "Товар с таким названием уже существует";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
