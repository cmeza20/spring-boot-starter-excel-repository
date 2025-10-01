package com.cmeza.spring.excel.repository.utils;

import com.cmeza.spring.excel.repository.support.exceptions.FieldConverterException;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

@UtilityClass
@SuppressWarnings("all")
public class ReflectionUtils {

    public Field findFieldFromObject(Class<?> clazz, String fieldName) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> f.getName().equals(fieldName))
                .peek(f -> f.setAccessible(true))
                .findFirst()
                .orElseThrow(() -> new FieldConverterException(String.format("Attribute '%s' was not found in class '%s'", fieldName, clazz.getName())));
    }

    public Method findMethodFromObject(Class<?> clazz, String fieldName, String prefix) {
        String methodName = prefix + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(f -> f.getName().equals(methodName))
                .peek(f -> f.setAccessible(true))
                .findFirst()
                .orElseThrow(() -> new FieldConverterException(String.format("Method '%s' was not found in class '%s'", methodName, clazz.getName())));
    }

    public <C, E> boolean hasFieldAnnotation(Class<?> clazz, Map<Class<C>, BiConsumer<C, E>> fieldAnnotations) {
        return Arrays.stream(clazz.getDeclaredFields()).anyMatch(field -> {
            return Arrays.stream(field.getAnnotations()).anyMatch(annotation -> Objects.nonNull(fieldAnnotations.get(annotation.annotationType())));
        });
    }
}
