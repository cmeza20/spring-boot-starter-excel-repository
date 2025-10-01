package com.cmeza.spring.excel.repository.resolvers;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

@RequiredArgsConstructor
public class ExcelPropertyResolverImpl implements ExcelPropertyResolver {

    private final Environment environment;

    @Override
    public String resolvePlaceholders(String key) {
        this.validate(key);
        return environment.resolvePlaceholders(key);
    }

    @Override
    public String resolveRequiredPlaceholders(String key) {
        this.validate(key);
        return environment.resolveRequiredPlaceholders(key);
    }

    private void validate(String key) {
        Assert.notNull(key, "Property key required!");
    }
}
