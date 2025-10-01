package com.cmeza.spring.excel.repository.resolvers;

public interface ExcelPropertyResolver {
    String resolvePlaceholders(String key);

    String resolveRequiredPlaceholders(String key);
}
