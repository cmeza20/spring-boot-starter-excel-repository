package com.cmeza.spring.excel.repository.configurations;

import com.cmeza.spring.excel.repository.dsl.properties.ExcelRepositoryProperties;
import com.cmeza.spring.excel.repository.interceptors.ExcelPropertyResolverInterceptor;
import com.cmeza.spring.excel.repository.interceptors.ExcelRepositoryTemplateInterceptor;
import com.cmeza.spring.excel.repository.support.annotations.ExcelRepository;
import com.cmeza.spring.excel.repository.support.annotations.methods.ToExcel;
import com.cmeza.spring.excel.repository.support.annotations.methods.ToModel;
import com.cmeza.spring.excel.repository.support.annotations.model.Sheet;
import com.cmeza.spring.excel.repository.support.annotations.model.Sheets;
import com.cmeza.spring.excel.repository.support.annotations.model.Style;
import com.cmeza.spring.excel.repository.support.annotations.model.Styles;
import com.cmeza.spring.excel.repository.aware.AwareBeanPostProcessor;
import com.cmeza.spring.excel.repository.contracts.ExcelContract;
import com.cmeza.spring.excel.repository.dsl.properties.DslProperties;
import com.cmeza.spring.excel.repository.processors.classes.ExcelRepositoryAnnotatedClassProcessor;
import com.cmeza.spring.excel.repository.processors.classes.StyleAnnotatedClassProcessor;
import com.cmeza.spring.excel.repository.processors.methods.ToExcelAnnotatedMethodProcessor;
import com.cmeza.spring.excel.repository.processors.methods.ToModelAnnotatedMethodProcessor;
import com.cmeza.spring.excel.repository.processors.parameters.ToExcelParameterProcessor;
import com.cmeza.spring.excel.repository.processors.supports.SheetAnnotatedMethodProcessor;
import com.cmeza.spring.excel.repository.processors.supports.StyleAnnotatedMethodProcessor;
import com.cmeza.spring.excel.repository.resolvers.ExcelPropertyResolver;
import com.cmeza.spring.excel.repository.resolvers.ExcelPropertyResolverImpl;
import com.cmeza.spring.excel.repository.templates.ExcelRepositoryTemplate;
import com.cmeza.spring.ioc.handler.builders.Ioc;
import com.cmeza.spring.ioc.handler.configuration.IocAutoConfiguration;
import com.cmeza.spring.ioc.handler.contracts.IocContract;
import com.cmeza.spring.ioc.handler.handlers.IocMethodInterceptor;
import com.cmeza.spring.ioc.handler.processors.AnnotatedClassProcessor;
import com.cmeza.spring.ioc.handler.processors.AnnotatedMethodProcessor;
import com.cmeza.spring.ioc.handler.processors.AnnotatedParameterProcessor;
import com.cmeza.spring.ioc.handler.processors.SimpleParameterProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.List;

@AutoConfigureOrder(Ioc.IOC_ORDER + 1)
@AutoConfiguration(after = IocAutoConfiguration.class)
@EnableConfigurationProperties({ExcelRepositoryProperties.class, DslProperties.class})
public class ExcelRepositoryAutoConfiguration {

    @Bean
    public ExcelRepositoryTemplate excelRepositoryTemplate() {
        return new ExcelRepositoryTemplate();
    }

    @Bean
    public IocContract<ExcelRepository> excelContract(List<AnnotatedClassProcessor<?>> classProcessors, List<AnnotatedMethodProcessor<?>> methodProcessors, List<AnnotatedParameterProcessor<?>> parameterProcessors, List<SimpleParameterProcessor> simpleParameterProcessors) {
        return new ExcelContract(classProcessors, methodProcessors, parameterProcessors, simpleParameterProcessors);
    }

    @Bean
    public AnnotatedClassProcessor<ExcelRepository> excelRepositoryAnnotatedClassProcessor() {
        return new ExcelRepositoryAnnotatedClassProcessor();
    }

    @Bean
    public AnnotatedClassProcessor<Style> styleAnnotatedClassProcessor() {
        return new StyleAnnotatedClassProcessor();
    }

    @Bean
    public AnnotatedClassProcessor<Styles> stylesAnnotatedClassProcessor() {
        return new StyleAnnotatedClassProcessor.ClassStyleRepeatable();
    }

    @Bean
    public AnnotatedMethodProcessor<ToExcel> toExcelAnnotatedMethodProcessor(DslProperties dslProperties) {
        return new ToExcelAnnotatedMethodProcessor(dslProperties);
    }

    @Bean
    public AnnotatedMethodProcessor<ToModel> toModelAnnotatedMethodProcessor(DslProperties dslProperties) {
        return new ToModelAnnotatedMethodProcessor<>(dslProperties);
    }

    @Bean
    public AnnotatedMethodProcessor<Sheet> sheetAnnotatedMethodProcessor() {
        return new SheetAnnotatedMethodProcessor();
    }

    @Bean
    public AnnotatedMethodProcessor<Sheets> sheetsAnnotatedMethodProcessor() {
        return new SheetAnnotatedMethodProcessor.MethodSheetRepeatable();
    }

    @Bean
    public AnnotatedMethodProcessor<Style> styleAnnotatedMethodProcessor() {
        return new StyleAnnotatedMethodProcessor();
    }

    @Bean
    public AnnotatedMethodProcessor<Styles> stylesAnnotatedMethodProcessor() {
        return new StyleAnnotatedMethodProcessor.MethodStyleRepeatable();
    }

    @Bean
    public SimpleParameterProcessor toExcelParameterProcessor(ExcelRepositoryProperties excelRepositoryProperties) {
        return new ToExcelParameterProcessor(excelRepositoryProperties);
    }

    @Bean
    public IocMethodInterceptor<ExcelPropertyResolver> excelPropertyResolverInterceptor() {
        return new ExcelPropertyResolverInterceptor();
    }

    @Bean
    public IocMethodInterceptor<ExcelRepositoryTemplate> excelRepositoryTemplateInterceptor() {
        return new ExcelRepositoryTemplateInterceptor();
    }

    @Bean
    public BeanPostProcessor awareBeanPostProcessor(ApplicationContext applicationContext) {
        return new AwareBeanPostProcessor(applicationContext);
    }

    @Bean
    public ExcelPropertyResolver excelPropertyResolver(Environment environment) {
        return new ExcelPropertyResolverImpl(environment);
    }
}
