package com.cmeza.spring.excel.repository.processors.parameters;

import com.cmeza.spring.excel.repository.parsers.excel.ExcelParser;
import com.cmeza.spring.excel.repository.parsers.excel.SheetParser;
import com.cmeza.spring.excel.repository.parsers.excel.StyleParser;
import com.cmeza.spring.excel.repository.support.annotations.model.Column;
import com.cmeza.spring.excel.repository.support.annotations.model.Sheet;
import com.cmeza.spring.excel.repository.support.annotations.model.Style;
import com.cmeza.spring.excel.repository.support.annotations.model.Styles;
import com.cmeza.spring.excel.repository.builders.ModelToExcelBuilder;
import com.cmeza.spring.excel.repository.configurations.ExcelRepositoryProperties;
import com.cmeza.spring.excel.repository.contracts.ExcelContract;
import com.cmeza.spring.excel.repository.support.converters.excel.ExcelSheetConverter;
import com.cmeza.spring.excel.repository.support.configurations.excel.ExcelConfiguration;
import com.cmeza.spring.excel.repository.support.configurations.excel.SheetConfiguration;
import com.cmeza.spring.excel.repository.support.configurations.excel.StyleConfiguration;
import com.cmeza.spring.excel.repository.repositories.executors.ExcelExecutor;
import com.cmeza.spring.excel.repository.repositories.executors.types.ExecutorType;
import com.cmeza.spring.excel.repository.support.parsers.Parser;
import com.cmeza.spring.excel.repository.utils.BeanUtils;
import com.cmeza.spring.ioc.handler.metadata.ClassMetadata;
import com.cmeza.spring.ioc.handler.metadata.MethodMetadata;
import com.cmeza.spring.ioc.handler.metadata.ParameterMetadata;
import com.cmeza.spring.ioc.handler.metadata.TypeMetadata;
import com.cmeza.spring.ioc.handler.processors.SimpleParameterProcessor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Parameter;
import java.util.*;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class ToExcelParameterProcessor implements SimpleParameterProcessor, ApplicationContextAware {
    private static final Parser PARSER = Parser.getInstance();
    private final ExcelRepositoryProperties excelRepositoryProperties;
    private ApplicationContext applicationContext;

    @Override
    public void process(Parameter parameter, ClassMetadata classMetadata, MethodMetadata methodMetadata, ParameterMetadata parameterMetadata, int index) {
        ExcelExecutor executor = methodMetadata.getAttribute(ExcelContract.METHOD_EXECUTOR, ExcelExecutor.class);
        TypeMetadata typeMetadata = parameterMetadata.getTypeMetadata();

        ExecutorType executorType = executor.getExecuteType();

        if (executorType.equals(ExecutorType.MODEL_TO_EXCEL) && (typeMetadata.isList() || typeMetadata.isSet() || typeMetadata.isStream())) {

            Class<?> clazz = parameterMetadata.getTypeMetadata().getArgumentClass();

            //Converter
            ExcelSheetConverter sheet = this.findConverterAndProcessGlobalConfiguration(methodMetadata, index);

            //Method styles
            this.bindMethodStyleConfigurations(methodMetadata, sheet);

            //Sheet configurations
            this.bindSheetConfigurations(methodMetadata, sheet, index);

            //Sheet annotation
            this.bindSheetAnnotation(sheet);

            //Style annotation
            this.bindStyleAnnotation(sheet);

            //Column annotation
            this.bindColumnAnnotation(sheet);

            sheet.preBuilt(clazz);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private ExcelSheetConverter findConverterAndProcessGlobalConfiguration(MethodMetadata methodMetadata, int index) {
        ModelToExcelBuilder builder = methodMetadata.getAttribute(ExcelContract.METHOD_BUILDER, ModelToExcelBuilder.class);
        if (StringUtils.isNotEmpty(excelRepositoryProperties.getGlobalExcelConfigurationBean())) {
            ExcelConfiguration globalExcelConfiguration = BeanUtils.findBean(applicationContext, ExcelConfiguration.class, excelRepositoryProperties.getGlobalExcelConfigurationBean());

            builder.withConfiguration(excelConfiguration -> {
                PARSER.getParser(ExcelParser.class).merge(globalExcelConfiguration, excelConfiguration);
                if (Objects.nonNull(excelConfiguration.getInterceptor())) {
                    Arrays.stream(excelConfiguration.getInterceptor()).forEach(interceptor ->
                            builder.withInterceptor(BeanUtils.findBean(applicationContext, interceptor)));
                }
                if (Objects.nonNull(excelConfiguration.getMapper())) {
                    Arrays.stream(excelConfiguration.getMapper()).forEach(mapper ->
                            builder.withMapper(BeanUtils.findBean(applicationContext, mapper)));
                }
            });
        }
        return builder.sheet(index);
    }

    private void bindSheetConfigurations(MethodMetadata methodMetadata, ExcelSheetConverter excelSheetConverter, int index) {
        Map<Integer, SheetConfiguration> sheetConfigurations = methodMetadata.getAttribute(ExcelContract.METHOD_SHEET_CONFIGURATIONS, Map.class);
        SheetConfiguration methodSheetConfiguration = sheetConfigurations.get(index);
        SheetConfiguration customSheetConfiguration = null;

        if (StringUtils.isNotEmpty(excelRepositoryProperties.getGlobalSheetConfigurationBean())) {
            SheetConfiguration globalSheetConfiguration = BeanUtils.findBean(applicationContext, SheetConfiguration.class, excelRepositoryProperties.getGlobalSheetConfigurationBean());
            if (Objects.nonNull(globalSheetConfiguration)) {
                customSheetConfiguration = globalSheetConfiguration.cloneInstance();
            }

            if (Objects.nonNull(customSheetConfiguration) && Objects.nonNull(methodSheetConfiguration)) {
                PARSER.getParser(SheetParser.class).merge(methodSheetConfiguration, customSheetConfiguration);
            } else if (Objects.isNull(customSheetConfiguration)) {
                customSheetConfiguration = methodSheetConfiguration;
            }
        } else {
            customSheetConfiguration = methodSheetConfiguration;
        }

        if (Objects.nonNull(customSheetConfiguration)) {
            this.bindSimpleSheetConfiguration(excelSheetConverter, customSheetConfiguration);
        }
    }

    private void bindSimpleSheetConfiguration(ExcelSheetConverter sheetConverter, SheetConfiguration sheetConfigurationBean) {
        sheetConverter.withSheetConfiguration(sheetConfiguration -> PARSER.getParser(SheetParser.class).merge(sheetConfigurationBean, sheetConfiguration));
    }

    private void bindMethodStyleConfigurations(MethodMetadata methodMetadata, ExcelSheetConverter sheetConverter) {
        List<StyleConfiguration> methodStyleConfigurations = methodMetadata.getAttribute(ExcelContract.METHOD_STYLE_CONFIGURATIONS, List.class, new ArrayList<>());
        if (!methodStyleConfigurations.isEmpty()) {
            sheetConverter.withSheetConfiguration(sheetConfiguration ->
                    methodStyleConfigurations.forEach(sheetConfiguration::addStyle));
        }
    }

    private void bindSheetAnnotation(ExcelSheetConverter sheetConverter) {
        sheetConverter.withSheetAnnotation(Sheet.class, (sheet, sheetConfiguration) -> {

            SheetParser sheetParser = PARSER.getParser(SheetParser.class);

            if (StringUtils.isEmpty(sheet.value())) {
                sheetParser.parse(sheet, sheetConfiguration, false);
                return;
            }

            SheetConfiguration sheetConfigurationBean = BeanUtils.findBean(applicationContext, SheetConfiguration.class, sheet.value(), false);
            if (Objects.isNull(sheetConfigurationBean)) {
                sheetParser.parse(sheet, sheetConfiguration, false);
                return;
            }

            SheetConfiguration cloned = sheetConfigurationBean.cloneInstance();
            sheetParser.parse(sheet, cloned, true);
            sheetParser.merge(cloned, sheetConfiguration);
        });
    }

    private void bindStyleConfiguration(Style excelStyle, SheetConfiguration sheetConfiguration) {
        if (StringUtils.isEmpty(excelStyle.name())) {
            applyStyleConfiguration(excelStyle, new StyleConfiguration(), sheetConfiguration, false);
            return;
        }

        StyleConfiguration styleConfigurationBean = BeanUtils.findBean(applicationContext, StyleConfiguration.class, excelStyle.name(), false);
        if (Objects.isNull(styleConfigurationBean)) {
            applyStyleConfiguration(excelStyle, new StyleConfiguration(), sheetConfiguration, false);
            return;
        }

        applyStyleConfiguration(excelStyle, styleConfigurationBean.cloneInstance(), sheetConfiguration, true);
    }

    private void applyStyleConfiguration(Style style, StyleConfiguration styleConfigurationBean, SheetConfiguration sheetConfiguration, boolean isBean) {
        PARSER.getParser(StyleParser.class).parse(style, styleConfigurationBean, isBean);
        sheetConfiguration.addStyle(styleConfigurationBean);
    }

    private void bindStyleAnnotation(ExcelSheetConverter sheetConverter) {
        sheetConverter.withSheetAnnotation(Style.class, this::bindStyleConfiguration);
        sheetConverter.withSheetAnnotation(Styles.class, (excelStyles, sheetConfiguration) -> {
            for (Style style : excelStyles.value()) {
                bindStyleConfiguration(style, sheetConfiguration);
            }
        });
    }

    private void bindColumnAnnotation(ExcelSheetConverter sheetConverter) {
        sheetConverter.withFieldAnnotation(Column.class, (column, fieldConfiguration) -> {
            if (StringUtils.isNotEmpty(column.header())) {
                fieldConfiguration.setHeaderName(column.header());
            }
            if (StringUtils.isNotEmpty(column.value())) {
                fieldConfiguration.setHeaderName(column.value());
            }
            if (StringUtils.isNotEmpty(column.mapping())) {
                fieldConfiguration.setMapping(column.mapping());
            }
            if (column.ignored()) {
                fieldConfiguration.setIgnored(true);
            }
            if (StringUtils.isNotEmpty(column.styleName())) {
                fieldConfiguration.setStyleAlias(column.styleName());
            }
            if (column.styleRow().length > 0) {
                fieldConfiguration.setStyleRowPosition(column.styleRow());
            }
        });
    }

}
