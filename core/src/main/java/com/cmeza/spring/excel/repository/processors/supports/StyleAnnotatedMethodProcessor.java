package com.cmeza.spring.excel.repository.processors.supports;

import com.cmeza.spring.excel.repository.parsers.excel.StyleParser;
import com.cmeza.spring.excel.repository.support.annotations.ExcelRepository;
import com.cmeza.spring.excel.repository.support.annotations.model.Style;
import com.cmeza.spring.excel.repository.support.annotations.model.Styles;
import com.cmeza.spring.excel.repository.contracts.ExcelContract;
import com.cmeza.spring.excel.repository.support.configurations.excel.StyleConfiguration;
import com.cmeza.spring.excel.repository.processors.abstracts.AbstractSupportMethodProcessor;
import com.cmeza.spring.excel.repository.support.parsers.Parser;
import com.cmeza.spring.excel.repository.utils.BeanUtils;
import com.cmeza.spring.ioc.handler.metadata.ClassMetadata;
import com.cmeza.spring.ioc.handler.metadata.MethodMetadata;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class StyleAnnotatedMethodProcessor extends AbstractSupportMethodProcessor<Style> {
    private static final Parser PARSER = Parser.getInstance();

    private static StyleConfiguration applyStyleConfiguration(Style annotation, StyleConfiguration styleConfiguration, boolean isBean) {
        PARSER.getParser(StyleParser.class).parse(annotation, styleConfiguration, isBean);
        return styleConfiguration;
    }

    public static StyleConfiguration generateConfiguration(ApplicationContext applicationContext, Style annotation) {

        StyleConfiguration styleConfigurationBean = BeanUtils.findBean(applicationContext, StyleConfiguration.class, annotation.name(), false);
        if (Objects.isNull(styleConfigurationBean)) {
            return applyStyleConfiguration(annotation, new StyleConfiguration(), false);
        }

        return applyStyleConfiguration(annotation, styleConfigurationBean.cloneInstance(), true);
    }

    @Override
    protected void annotationProcess(ExcelRepository excelRepository, Style annotation, ClassMetadata classMetadata, MethodMetadata methodMetadata, Map<String, Object> annotationValues) {
        //Custom attributes
    }

    @Override
    protected Object bindDefinition(Style annotation, ClassMetadata classMetadata, MethodMetadata methodMetadata) {
        List<StyleConfiguration> styleConfigurations = methodMetadata.getAttribute(tagUniqueSupport(), List.class);

        StyleConfiguration styleConfiguration = generateConfiguration(applicationContext, annotation);

        styleConfigurations.add(styleConfiguration);
        return styleConfigurations;
    }

    @Override
    protected String tagUniqueSupport() {
        return ExcelContract.METHOD_STYLE_CONFIGURATIONS;
    }

    @Override
    protected boolean isUnique() {
        return false;
    }

    public static class MethodStyleRepeatable extends AbstractSupportMethodProcessor<Styles> {

        @Override
        protected void annotationProcess(ExcelRepository excelRepository, Styles annotation, ClassMetadata classMetadata, MethodMetadata methodMetadata, Map<String, Object> annotationValues) {
            //Custom attributes
        }

        @Override
        protected Object bindDefinition(Styles annotation, ClassMetadata classMetadata, MethodMetadata methodMetadata) {
            List<StyleConfiguration> styleConfigurations = methodMetadata.getAttribute(tagUniqueSupport(), List.class);

            for (Style style : annotation.value()) {
                StyleConfiguration styleConfiguration = generateConfiguration(applicationContext, style);

                styleConfigurations.add(styleConfiguration);
            }

            return styleConfigurations;
        }

        @Override
        protected String tagUniqueSupport() {
            return ExcelContract.METHOD_STYLE_CONFIGURATIONS;
        }
    }
}
