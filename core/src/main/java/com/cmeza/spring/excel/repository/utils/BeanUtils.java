package com.cmeza.spring.excel.repository.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@UtilityClass
public class BeanUtils {

    public <T> T findBean(ApplicationContext applicationContext, Class<T> clazz, String beanName) {
        return findBean(applicationContext, clazz, beanName, true);
    }

    public <T> T findBean(ApplicationContext applicationContext, Class<T> clazz, String beanName, boolean required) {
        if (StringUtils.isEmpty(beanName)) {
            if (required) {
                throw new NoSuchBeanDefinitionException("Bean name is required");
            }
            return null;
        }
        Map<String, T> beansOfType = applicationContext.getBeansOfType(clazz);
        T bean = beansOfType.get(beanName);
        if (required && Objects.isNull(bean)) {
            throw new NoSuchBeanDefinitionException(beanName);
        }

        return bean;
    }

    public <T> T findBean(ApplicationContext applicationContext, Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public Optional<Object> getBeanFromInterceptor(ApplicationContext applicationContext, Method method) {
        Qualifier qualifier = method.getAnnotation(Qualifier.class);
        if (Objects.nonNull(qualifier)) {
            return Optional.of(applicationContext.getBean(qualifier.value()));
        }

        if (applicationContext.containsBean(method.getName())) {
            return Optional.of(applicationContext.getBean(method.getName()));
        }

        return Optional.empty();
    }
}
