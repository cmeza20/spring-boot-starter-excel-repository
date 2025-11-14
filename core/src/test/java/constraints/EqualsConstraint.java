package constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class EqualsConstraint implements ConstraintValidator<Equals, String> {

    private String value;

    @Override
    public void initialize(Equals constraintAnnotation) {
        value = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return Objects.nonNull(s) && s.contains(value);
    }
}
