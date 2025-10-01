package com.cmeza.spring.excel.repository.aware;

import com.cmeza.spring.excel.repository.resolvers.ExcelPropertyResolver;
import com.cmeza.spring.excel.repository.templates.ExcelRepositoryTemplate;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.Objects;

public class AwareBeanPostProcessor implements BeanPostProcessor {

    private final ApplicationContext applicationContext;

    public AwareBeanPostProcessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if (bean instanceof ExcelRepositoryAware) {
            ExcelRepositoryAware excelRepositoryAware = (ExcelRepositoryAware) bean;

            Map<String, ExcelRepositoryTemplate> jdbcRepositoryTemplates = applicationContext.getBeansOfType(ExcelRepositoryTemplate.class);

            ExcelRepositoryTemplate excelRepositoryTemplate = jdbcRepositoryTemplates.get(excelRepositoryAware.getQualifier());
            if (Objects.isNull(excelRepositoryTemplate)) {
                throw new BeanInstantiationException(bean.getClass(), "No bean qualifier for: " + excelRepositoryAware.getQualifier());
            }
            excelRepositoryAware.setPropertiesResolver(applicationContext.getBean(ExcelPropertyResolver.class));
            excelRepositoryAware.setExcelRepositoryTemplate(excelRepositoryTemplate);
            return bean;
        }
        return bean;
    }
}
