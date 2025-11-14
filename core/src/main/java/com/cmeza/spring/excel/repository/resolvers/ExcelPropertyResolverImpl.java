package com.cmeza.spring.excel.repository.resolvers;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

@RequiredArgsConstructor
public class ExcelPropertyResolverImpl implements ExcelPropertyResolver {

    private final Environment environment;

    @Override
    public String resolvePlaceholders(String key) {
        return this.resolvePlaceholders(key, null);
    }

    @Override
    public String resolvePlaceholders(String key, String message) {
        this.validateKey(key, message);
        return environment.resolvePlaceholders(key);
    }

    @Override
    public String resolveRequiredPlaceholders(String key) {
        return this.resolveRequiredPlaceholders(key, null);
    }

    @Override
    public String resolveRequiredPlaceholders(String key, String message) {
        this.validateKey(key, message);
        return environment.resolveRequiredPlaceholders(key);
    }

    private void validateKey(String key, String customMessage) {
        Assert.notNull(key, StringUtils.isEmpty(customMessage) ? "Property key required!" : customMessage);
    }
}
