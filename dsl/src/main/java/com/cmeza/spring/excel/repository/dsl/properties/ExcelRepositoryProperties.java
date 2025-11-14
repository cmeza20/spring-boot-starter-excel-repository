package com.cmeza.spring.excel.repository.dsl.properties;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@ConfigurationProperties("spring.excel.repository")
public class ExcelRepositoryProperties {
    public static final String EXCEL_DEFAULT_FOLDER = "classpath:/excel/**";

    /**
     * Folder where the queries and properties are stored (String)
     */
    @NotNull
    private final String excelFolder;

    /**
     * Global track logger (Boolean)
     */
    private final boolean loggable;

    /**
     * Search only declared methods (Boolean)
     */
    private final boolean onlyDeclaredMethods;

    /**
     * Search only for annotations declared in the method (Boolean)
     */
    private final boolean onlyMethodDeclaredAnnotations;

    /**
     * Search only annotations declared in the parameter (Boolean)
     */
    private final boolean onlyParameterDeclaredAnnotations;

    /**
     * Global bean excel configuration (String)
     */
    private final String globalExcelConfigurationBean;

    /**
     * Global bean class configuration (String)
     */
    private final String globalSheetConfigurationBean;

    /**
     * Global bean style configurations (String[])
     */
    private final String[] globalStyleConfigurationBean;

    @ConstructorBinding
    public ExcelRepositoryProperties(
            @DefaultValue(EXCEL_DEFAULT_FOLDER) String excelFolder,
            @DefaultValue("false") boolean loggable,
            @DefaultValue("false") boolean onlyDeclaredMethods,
            @DefaultValue("false") boolean onlyMethodDeclaredAnnotations,
            @DefaultValue("false") boolean onlyParameterDeclaredAnnotations,
            String globalExcelConfigurationBean,
            String globalSheetConfigurationBean,
            String[] globalStyleConfigurationBean) {
        this.excelFolder = excelFolder;
        this.loggable = loggable;
        this.onlyDeclaredMethods = onlyDeclaredMethods;
        this.onlyMethodDeclaredAnnotations = onlyMethodDeclaredAnnotations;
        this.onlyParameterDeclaredAnnotations = onlyParameterDeclaredAnnotations;
        this.globalExcelConfigurationBean = globalExcelConfigurationBean;
        this.globalSheetConfigurationBean = globalSheetConfigurationBean;
        this.globalStyleConfigurationBean = globalStyleConfigurationBean;
    }
}
