package com.cmeza.spring.excel.repository.repositories.executors.types;

import com.cmeza.spring.excel.repository.support.results.*;
import com.cmeza.spring.ioc.handler.metadata.TypeMetadata;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.servlet.View;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum ReturnType {
    LIST(List.class),
    SET(Set.class),
    STREAM(Stream.class),
    VIEW(View.class),
    PATH(Path.class),
    FILE(File.class),
    VALIDATED(Validated.class),
    MAP_VALIDATED(MapValidated.class),
    EXCEL_VALIDATED(ExcelValidated.class),
    MAP_EXCEL_VALIDATED(MapExcelValidated.class),
    MAP_VIEW_VALIDATED(MapViewValidated.class),
    VIEW_VALIDATED(ViewValidated.class),
    VALIDATED_ERROR(ValidatedError.class),
    MAP_VALIDATED_ERROR(MapValidatedError.class),
    EXCEL_VALIDATED_ERROR(ExcelValidatedError.class),
    MAP_EXCEL_VALIDATED_ERROR(MapExcelValidatedError.class),
    MAP_VIEW_VALIDATED_ERROR(MapViewValidatedError.class),
    VIEW_VALIDATED_ERROR(ViewValidatedError.class),
    UNSUPPORTED(null);

    final Class<?> clazz;

    public static ReturnType forToExcel(TypeMetadata typeMetadata) {
        if (typeMetadata.getRawClass().isAssignableFrom(View.class)) {
            return VIEW;
        }
        if (typeMetadata.getRawClass().isAssignableFrom(Path.class)) {
            return PATH;
        }
        if (typeMetadata.getRawClass().isAssignableFrom(File.class)) {
            return FILE;
        }
        return UNSUPPORTED;
    }

    public static ReturnType forToModel(TypeMetadata typeMetadata) {
        if (typeMetadata.isList()) {
            return LIST;
        }
        if (typeMetadata.isSet()) {
            return SET;
        }
        if (typeMetadata.isStream()) {
            return STREAM;
        }
        if (typeMetadata.getRawClass().isAssignableFrom(Validated.class)) {
            return VALIDATED;
        }
        if (typeMetadata.getRawClass().isAssignableFrom(ValidatedError.class)) {
            return VALIDATED_ERROR;
        }
        if (typeMetadata.getRawClass().isAssignableFrom(MapValidated.class)) {
            return MAP_VALIDATED;
        }
        if (typeMetadata.getRawClass().isAssignableFrom(MapValidatedError.class)) {
            return MAP_VALIDATED_ERROR;
        }
        if (typeMetadata.getRawClass().isAssignableFrom(ExcelValidated.class)) {
            return EXCEL_VALIDATED;
        }
        if (typeMetadata.getRawClass().isAssignableFrom(ExcelValidatedError.class)) {
            return EXCEL_VALIDATED_ERROR;
        }
        if (typeMetadata.getRawClass().isAssignableFrom(MapExcelValidated.class)) {
            return MAP_EXCEL_VALIDATED;
        }
        if (typeMetadata.getRawClass().isAssignableFrom(MapExcelValidatedError.class)) {
            return MAP_EXCEL_VALIDATED_ERROR;
        }
        if (typeMetadata.getRawClass().isAssignableFrom(MapViewValidatedError.class)) {
            return MAP_VIEW_VALIDATED_ERROR;
        }
        if (typeMetadata.getRawClass().isAssignableFrom(ViewValidated.class)) {
            return VIEW_VALIDATED;
        }
        if (typeMetadata.getRawClass().isAssignableFrom(ViewValidatedError.class)) {
            return VIEW_VALIDATED_ERROR;
        }
        return UNSUPPORTED;
    }

}
