package com.cmeza.spring.excel.repository.resolvers;

public interface ExcelPropertyResolver {
    String resolvePlaceholders(String key);

    String resolvePlaceholders(String key, String message);

    String resolveRequiredPlaceholders(String key);

    String resolveRequiredPlaceholders(String key, String message);
}
