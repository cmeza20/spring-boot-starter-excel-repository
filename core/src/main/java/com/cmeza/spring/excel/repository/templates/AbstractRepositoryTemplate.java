package com.cmeza.spring.excel.repository.templates;

import com.cmeza.spring.excel.repository.ExcelRepositoryOperations;
import com.cmeza.spring.excel.repository.builders.abstracts.AbstractBuilderExcel;
import com.cmeza.spring.excel.repository.dialects.DefaultDialect;
import com.cmeza.spring.excel.repository.dsl.properties.ExcelRepositoryProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public abstract class AbstractRepositoryTemplate implements ApplicationContextAware, BeanNameAware {

    protected ApplicationContext applicationContext;
    protected ExcelRepositoryProperties excelRepositoryProperties;
    private ExcelRepositoryOperations excelRepositoryOperations;
    private String beanName;

    private void initDialect() {
        AbstractBuilderExcel.Impl impl = new AbstractBuilderExcel.Impl((ExcelRepositoryTemplate) this, excelRepositoryProperties, applicationContext, beanName);
        excelRepositoryOperations = new DefaultDialect(impl);
    }

    public ExcelRepositoryOperations getDialect() {
        return excelRepositoryOperations;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.excelRepositoryProperties = applicationContext.getBean(ExcelRepositoryProperties.class);
        this.initDialect();
    }

    @Override
    public void setBeanName(String s) {
        this.beanName = s;
    }


}
