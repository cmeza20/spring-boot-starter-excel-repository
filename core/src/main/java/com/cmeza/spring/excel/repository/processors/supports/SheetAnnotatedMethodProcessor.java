package com.cmeza.spring.excel.repository.processors.supports;

import com.cmeza.spring.excel.repository.parsers.excel.SheetParser;
import com.cmeza.spring.excel.repository.support.annotations.ExcelRepository;
import com.cmeza.spring.excel.repository.support.annotations.model.Sheet;
import com.cmeza.spring.excel.repository.support.annotations.model.Sheets;
import com.cmeza.spring.excel.repository.contracts.ExcelContract;
import com.cmeza.spring.excel.repository.support.configurations.excel.SheetConfiguration;
import com.cmeza.spring.excel.repository.processors.abstracts.AbstractSupportMethodProcessor;
import com.cmeza.spring.excel.repository.support.parsers.Parser;
import com.cmeza.spring.excel.repository.utils.BeanUtils;
import com.cmeza.spring.ioc.handler.metadata.ClassMetadata;
import com.cmeza.spring.ioc.handler.metadata.MethodMetadata;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.Objects;

public class SheetAnnotatedMethodProcessor extends AbstractSupportMethodProcessor<Sheet> {
    private static final Parser PARSER = Parser.getInstance();

    private static SheetConfiguration applySheetConfiguration(Sheet annotation, SheetConfiguration sheetConfiguration, boolean isBean) {
        SheetParser sheetParser = PARSER.getParser(SheetParser.class);
        sheetParser.parse(annotation, sheetConfiguration, isBean);
        return sheetConfiguration;
    }

    private static SheetConfiguration generateConfiguration(ApplicationContext applicationContext, Sheet annotation) {
        if (StringUtils.isEmpty(annotation.value())) {
            return applySheetConfiguration(annotation, new SheetConfiguration(), false);
        }

        SheetConfiguration sheetConfigurationBean = BeanUtils.findBean(applicationContext, SheetConfiguration.class, annotation.value(), false);
        if (Objects.isNull(sheetConfigurationBean)) {
            return applySheetConfiguration(annotation, new SheetConfiguration(), false);
        }

        return applySheetConfiguration(annotation, sheetConfigurationBean.cloneInstance(), true);
    }

    @Override
    protected void annotationProcess(ExcelRepository excelRepository, Sheet annotation, ClassMetadata classMetadata, MethodMetadata methodMetadata, Map<String, Object> annotationValues) {
        //Custom attributes
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Object bindDefinition(Sheet annotation, ClassMetadata classMetadata, MethodMetadata methodMetadata) {
        Map<Integer, SheetConfiguration> sheetConfigurations = methodMetadata.getAttribute(tagUniqueSupport(), Map.class);

        SheetConfiguration sheetConfiguration = generateConfiguration(applicationContext, annotation);

        sheetConfigurations.put(sheetConfigurations.size(), sheetConfiguration);
        return sheetConfigurations;
    }

    @Override
    protected String tagUniqueSupport() {
        return ExcelContract.METHOD_SHEET_CONFIGURATIONS;
    }

    @Override
    protected boolean isUnique() {
        return false;
    }

    public static class MethodSheetRepeatable extends AbstractSupportMethodProcessor<Sheets> {

        @Override
        protected void annotationProcess(ExcelRepository excelRepository, Sheets annotation, ClassMetadata classMetadata, MethodMetadata methodMetadata, Map<String, Object> annotationValues) {
            //Custom attributes
        }

        @Override
        @SuppressWarnings("unchecked")
        protected Object bindDefinition(Sheets annotation, ClassMetadata classMetadata, MethodMetadata methodMetadata) {
            Map<Integer, SheetConfiguration> sheetConfigurations = methodMetadata.getAttribute(tagUniqueSupport(), Map.class);

            for (Sheet sheetAnnotation : annotation.value()) {
                SheetConfiguration sheetConfiguration = generateConfiguration(applicationContext, sheetAnnotation);
                sheetConfigurations.put(sheetConfigurations.size(), sheetConfiguration);
            }

            return sheetConfigurations;
        }

        @Override
        protected String tagUniqueSupport() {
            return ExcelContract.METHOD_SHEET_CONFIGURATIONS;
        }
    }
}
