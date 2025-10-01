package constraints;

import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
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
