package constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy= EqualsConstraint.class)
@Documented
public @interface Equals {
    String value();

    String message() default "Constraint does not match";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
