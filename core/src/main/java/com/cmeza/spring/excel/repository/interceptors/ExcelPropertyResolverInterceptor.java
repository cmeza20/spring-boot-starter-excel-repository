package com.cmeza.spring.excel.repository.interceptors;

import com.cmeza.spring.excel.repository.resolvers.ExcelPropertyResolver;
import com.cmeza.spring.excel.repository.utils.BeanUtils;
import com.cmeza.spring.ioc.handler.handlers.IocMethodInterceptor;
import com.cmeza.spring.ioc.handler.handlers.IocTarget;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.util.Optional;

public class ExcelPropertyResolverInterceptor implements IocMethodInterceptor<ExcelPropertyResolver>, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public Optional<Object> invoke(Object o, IocTarget<?> iocTarget, Method method, Object[] objects) {
        Optional<Object> beanFromInterceptor = BeanUtils.getBeanFromInterceptor(applicationContext, method);

        if (beanFromInterceptor.isPresent()) {
            return beanFromInterceptor;
        }

        return Optional.of(applicationContext.getBean(ExcelPropertyResolver.class));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
