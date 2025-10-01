package com.cmeza.spring.excel.repository.utils;

import com.cmeza.spring.ioc.handler.metadata.TypeMetadata;
import com.cmeza.spring.ioc.handler.metadata.impl.SimpleTypeMetadata;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

@UtilityClass
public final class LoggerUtils {

    public String printReturnType(TypeMetadata typeMetadata) {
        StringBuilder stringBuilder = new StringBuilder(typeMetadata.getRawClass().getSimpleName());
        if (typeMetadata.isParameterized()) {
            stringBuilder.append("<")
                    .append(Arrays.stream(typeMetadata.getArgumentTypes()).map(Class::getSimpleName).collect(Collectors.joining(", ")))
                    .append(">");
        }
        return stringBuilder.toString();
    }

    public void printHeaderLog(HeaderLog headerLog) {
        Logger logger = headerLog.getLogger();
        if (headerLog.isLoggable() && logger.isInfoEnabled()) {
            logger.info("|");
            logger.info("| Executor: {}", headerLog.getClassName());
            logger.info("| ExcelRepositoryTemplate: {}", headerLog.getExcelRepositoryTemplateBeanName());

            if (headerLog.hasKey()) {
                logger.info("| Key Identification: {}", headerLog.getKey());
            }

            headerLog.getPrintExtras().accept(logger);
        }
    }

    public void printResultType(Logger logger, boolean loggable, Object obj, long mill) {
        if (loggable && logger.isInfoEnabled()) {
            logger.info("| Time: {} ms", System.currentTimeMillis() - mill);
            if (obj != null) {
                TypeMetadata typeMetadata = new SimpleTypeMetadata(obj.getClass());
                String resultType = LoggerUtils.printReturnType(typeMetadata);
                logger.info("| Result Type: {}", resultType);
            }
        }
    }

    public void printResultValue(Logger logger, boolean loggable, Object obj) {
        if (loggable && logger.isInfoEnabled()) {
            if (obj != null) {
                logger.info("| Result: {}", obj);
            } else {
                logger.info("| Result: null");
            }
        }
    }

    public void printParametersLog(Logger logger, boolean loggable, Map<String, Class<?>> parameters) {
        if (loggable && logger.isInfoEnabled()) {
            logger.info("| Attributes: {}", parameters.size());

            for (Map.Entry<String, Class<?>> entry : parameters.entrySet()) {
                printParameterDetailLog(logger, entry.getKey(), entry.getValue());
            }
        }
    }

    public void printParameterDetailLog(Logger logger, String parameterName, Class<?> parameterType) {
        logger.info("| * {} - {}", parameterName, Objects.isNull(parameterType) ? "null" : parameterType.getSimpleName());
    }
}
