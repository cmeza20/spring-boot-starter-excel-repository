package com.cmeza.spring.excel.repository.utils;

import com.cmeza.spring.excel.repository.support.annotations.support.Error;
import com.cmeza.spring.excel.repository.dsl.models.ErrorDsl;
import com.cmeza.spring.excel.repository.support.exceptions.ExecuteUnsupportedException;
import com.cmeza.spring.excel.repository.support.exceptions.ModelException;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@UtilityClass
public class ModelUtils {

    public <T> void configureSimpleConsumers(List<Consumer<T>> genericConsumers, T configuration) {
        for (Consumer<T> consumer : genericConsumers) {
            consumer.accept(configuration);
        }
    }

    public <C extends Annotation, T> void configureAnnotations(AnnotatedElement classData, Map<Class<C>, BiConsumer<C, T>> genericAnnotations, T configuration) {
        for (Map.Entry<Class<C>, BiConsumer<C, T>> entry : genericAnnotations.entrySet()) {
            if (!classData.isAnnotationPresent(entry.getKey())) {
                continue;
            }

            C annotation = classData.getAnnotation(entry.getKey());
            entry.getValue().accept(annotation, configuration);
        }
    }

    public boolean isHierarchical(String attribute) {
        return attribute.contains(".");
    }

    @SuppressWarnings("unchecked")
    public <T> T convertTo(Object obj) {
        if (Objects.isNull(obj)) {
            throw new ModelException("Object is null");
        }

        return (T)obj;
    }

    @SuppressWarnings("all")
    public <T> T convertTo(Object obj, Class<T> type) {
        if (Objects.isNull(obj)) {
            throw new ModelException("Object is null");
        }

        return (T)obj;
    }

    public boolean hasDefaultBoolean(boolean value, boolean expectedValue) {
        return value == expectedValue;
    }

    public boolean hasDefaultString(String value, String expectedValue) {
        return Objects.isNull(value) || value.equals(expectedValue);
    }

    public boolean hasDefaultInteger(int value, int expectedValue) {
        return value == expectedValue;
    }

    public boolean hasDefaultClass(Class<?> value, Class<?> expectedValue) {
        return Objects.isNull(value) || value.equals(expectedValue);
    }

    public void updateDslError(ErrorDsl errorDsl, Error annotation) {
        if (!hasDefaultString(annotation.fileName(), "")) {
            errorDsl.setFileName(annotation.fileName());
        }

        if (!ModelUtils.hasDefaultString(annotation.folder(), "")) {
            errorDsl.setFolder(annotation.folder());
        }

        if (!ModelUtils.hasDefaultBoolean(annotation.versioned(), false)) {
            errorDsl.setVersioned(annotation.versioned());
        }
    }

    public File argsValidate(Object... args) {
        Object arg = args[0];
        if (Objects.isNull(arg)) {
            throw new ExecuteUnsupportedException("Argument is null");
        }

        if (!(arg instanceof Path) && !(arg instanceof File)) {
            throw new ExecuteUnsupportedException("Argument with class: " + arg.getClass() + " is not a path or file");
        }

        if (arg instanceof Path) {
            return ((Path) arg).toFile();
        }

        return (File) arg;
    }

}
