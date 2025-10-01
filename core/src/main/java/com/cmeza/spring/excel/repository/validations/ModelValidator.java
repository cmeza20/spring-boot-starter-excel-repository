package com.cmeza.spring.excel.repository.validations;

import com.cmeza.spring.excel.repository.support.validations.ModelConstraintViolation;
import com.cmeza.spring.excel.repository.support.validations.ModelValidatorFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ModelValidator {

    private jakarta.validation.Validator jakartaValidator;
    private javax.validation.Validator javaxValidator;
    private boolean initialized = false;

    private ModelValidatorFactory factory;
    private static final boolean HAS_VALIDATOR_PROVIDER;
    private static boolean isJakartaValidator;

    static {
        ClassLoader classLoader = ModelValidator.class.getClassLoader();
        HAS_VALIDATOR_PROVIDER = ClassUtils.isPresent("org.hibernate.validator.internal.engine.ValidatorImpl", classLoader);
        if (HAS_VALIDATOR_PROVIDER) {
            try {
                Class<?> validatorImplClass = ClassUtils.forName("org.hibernate.validator.internal.engine.ValidatorImpl", classLoader);
                Type[] genericInterfaces = validatorImplClass.getGenericInterfaces();
                if (genericInterfaces.length > 0) {
                    isJakartaValidator = genericInterfaces[0].getTypeName().contains("jakarta.validation");
                }
            } catch (Exception ignored) {
                //ignore
            }
        }
    }

    private ModelValidator(jakarta.validation.Validator jakartaValidator, javax.validation.Validator javaxValidator) {
        this.jakartaValidator = jakartaValidator;
        this.javaxValidator = javaxValidator;
        this.factory = new ModelValidatorFactory();
    }

    public static ModelValidator getInstance() {
        return new ModelValidator(null, null);
    }

    public ModelValidator withValidator(jakarta.validation.Validator jakartaValidator) {
        this.jakartaValidator = jakartaValidator;
        return this;
    }

    public ModelValidator withValidator(javax.validation.Validator javaxValidator) {
        this.javaxValidator = javaxValidator;
        return this;
    }

    public <T> Set<ModelConstraintViolation<T>> validateValue(Class<T> modelClass, String attributeName, Object value) {
        if (!HAS_VALIDATOR_PROVIDER) {
            return Collections.emptySet();
        }

        this.init();
        Set<ModelConstraintViolation<T>> results = new LinkedHashSet<>();
        if (Objects.nonNull(jakartaValidator)) {
            results.addAll(factory.makeJakartaConstraintViolation(jakartaValidator.validateValue(modelClass, attributeName, value)));
        }
        if (Objects.nonNull(javaxValidator)) {
            results.addAll(factory.makeJavaxConstraintViolation(javaxValidator.validateValue(modelClass, attributeName, value)));
        }
        return results;
    }

    private void init() {
        if (!initialized) {
            if (Objects.isNull(jakartaValidator) && isJakartaValidator) {
                try (jakarta.validation.ValidatorFactory validatorFactory = jakarta.validation.Validation.buildDefaultValidatorFactory()) {
                    jakartaValidator = validatorFactory.getValidator();
                }
            } else if (Objects.isNull(javaxValidator) && !isJakartaValidator) {
                try (javax.validation.ValidatorFactory validatorFactory = javax.validation.Validation.buildDefaultValidatorFactory()) {
                    javaxValidator = validatorFactory.getValidator();
                }
            }
            this.initialized = true;
        }
    }
}
