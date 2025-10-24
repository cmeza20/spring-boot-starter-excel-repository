package com.cmeza.spring.excel.repository.processors.methods;

import com.cmeza.spring.excel.repository.dsl.properties.DslProperties;
import com.cmeza.spring.excel.repository.parsers.excel.ExcelParser;
import com.cmeza.spring.excel.repository.support.annotations.ExcelRepository;
import com.cmeza.spring.excel.repository.support.annotations.methods.ToExcel;
import com.cmeza.spring.excel.repository.builders.ModelToExcelBuilder;
import com.cmeza.spring.excel.repository.support.configurations.excel.ExcelConfiguration;
import com.cmeza.spring.excel.repository.dsl.models.ToExcelDsl;
import com.cmeza.spring.excel.repository.processors.abstracts.AbstractAnnotatedMethodProcessor;
import com.cmeza.spring.excel.repository.repositories.executors.ExcelExecutor;
import com.cmeza.spring.excel.repository.repositories.executors.ModelToExcelExecutor;
import com.cmeza.spring.excel.repository.support.parsers.Parser;
import com.cmeza.spring.excel.repository.templates.ExcelRepositoryTemplate;
import com.cmeza.spring.excel.repository.utils.BeanUtils;
import com.cmeza.spring.ioc.handler.metadata.ClassMetadata;
import com.cmeza.spring.ioc.handler.metadata.MethodMetadata;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class ToExcelAnnotatedMethodProcessor extends AbstractAnnotatedMethodProcessor<ToExcel, ModelToExcelBuilder, ToExcelDsl> {

    public ToExcelAnnotatedMethodProcessor(DslProperties dslProperties) {
        super(dslProperties);
    }

    @Override
    protected void configure(ToExcel annotation, ExcelRepository excelRepository, ClassMetadata classMetadata, MethodMetadata methodMetadata) {
        //ignore
    }

    @Override
    protected ExcelExecutor executorProcess(ExcelRepository excelRepository, ToExcel annotation, ClassMetadata classMetadata, MethodMetadata methodMetadata) {
        return new ModelToExcelExecutor(annotation);
    }

    @Override
    protected ModelToExcelBuilder builder(ExcelRepository excelRepository, ToExcel annotation, ExcelRepositoryTemplate excelRepositoryTemplate, MethodMetadata methodMetadata) {
        ExcelConfiguration excelConfiguration = this.generateConfiguration(annotation);
        return excelRepositoryTemplate.toExcel()
                .withConfiguration(excelConfiguration);
    }

    @Override
    protected ToExcelDsl dslLocator(ToExcel annotation, DslProperties dslProperties, ClassMetadata classMetadata, MethodMetadata methodMetadata) {
        ToExcelDsl toExcelDsl = dslProperties.findToExcelDsl(classMetadata.getTargetClass().getSimpleName(), methodMetadata.getMethod().getName());
        Parser.getInstance().getParser(ExcelParser.class).parseDsl(annotation, toExcelDsl);
        return toExcelDsl;
    }

    @Override
    protected void resolvePlaceholders(ToExcelDsl dslProperty) {
        if (StringUtils.isNotEmpty(dslProperty.getPath())) {
            dslProperty.setPath(propertiesResolver.resolveRequiredPlaceholders(dslProperty.getPath()));
        }
    }

    @Override
    protected void updateValues(Map<String, Object> values, ToExcelDsl dslProperty) {
        //ignore
    }

    private ExcelConfiguration applyExcelConfiguration(ToExcel annotation, ExcelConfiguration excelConfiguration) {
        PARSER.getParser(ExcelParser.class).parse(annotation, excelConfiguration);
        return excelConfiguration;
    }

    private ExcelConfiguration generateConfiguration(ToExcel annotation) {
        String beanName = annotation.excelConfigurationBean();
        if (StringUtils.isEmpty(beanName)) {
            return applyExcelConfiguration(annotation, new ExcelConfiguration());
        }

        ExcelConfiguration excelConfigurationBean = BeanUtils.findBean(applicationContext, ExcelConfiguration.class, beanName);
        return applyExcelConfiguration(annotation, excelConfigurationBean.cloneInstance());
    }

}
