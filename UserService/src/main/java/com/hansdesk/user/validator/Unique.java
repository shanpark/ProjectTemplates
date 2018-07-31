package com.hansdesk.user.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueValidator.class)
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Unique {
    String message() default "The value is already used.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String table();
    String column();
}
