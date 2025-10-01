package com.cmeza.spring.excel.repository.support.configurations.excel;

import com.cmeza.spring.excel.repository.support.configurations.AbstractConfiguration;
import com.cmeza.spring.excel.repository.support.exceptions.ExcelException;
import com.cmeza.spring.excel.repository.support.interceptors.ToExcelInterceptor;
import com.cmeza.spring.excel.repository.support.mappers.ToExcelMapper;
import com.cmeza.spring.excel.repository.support.parsers.Parser;
import com.cmeza.spring.excel.repository.support.parsers.excel.IExcelParser;
import com.cmeza.spring.excel.repository.support.utils.SupportUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Data
public class ExcelConfiguration extends AbstractConfiguration<ExcelConfiguration> {
    private Path path;
    private String fileName;
    private String prefix;
    private boolean versioned;
    private Class<? extends ToExcelInterceptor>[] interceptor;
    private Class<? extends ToExcelMapper>[] mapper;
    @Setter(AccessLevel.PRIVATE)
    private boolean defaultName;
    @Setter(AccessLevel.PRIVATE)
    private List<StyleConfiguration> styles = new ArrayList<>();

    public ExcelConfiguration() {
        this.path = Path.of(System.getProperty("java.io.tmpdir"));
        this.defaultName = true;
    }

    public Path getFullPath() {
        this.validate();
        String fileNameWithPrefix = SupportUtils.makeFileNameWithPrefix(this.prefix, this.fileName, this.versioned, this.defaultName);
        return Path.of(this.path.toString(), fileNameWithPrefix);
    }

    public void validate() {
        if (defaultName) {
            this.fileName = SupportUtils.generateDefaultFileName("");
        }
        Assert.notNull(this.path, "The Path must not be null");
        Assert.hasLength(this.fileName, "The file name must not be empty");
        Assert.isTrue(PathUtils.isDirectory(this.path), "The path must be a directory");

        String ext = FilenameUtils.getExtension(this.fileName);
        if (StringUtils.isEmpty(ext) || !ext.equalsIgnoreCase(SupportUtils.EXTENSION)) {
            throw new ExcelException("The file name has an incorrect extension");
        }
    }

    public ExcelConfiguration addStyle(StyleConfiguration style) {
        Assert.notNull(style, "StyleConfiguration can not be null");
        Assert.hasLength(style.getAlias(), "StyleConfiguration::Alias is empty");
        styles.add(style);
        return this;
    }

    @Override
    public ExcelConfiguration cloneInstance() {
        ExcelConfiguration clone = new ExcelConfiguration();
        Parser.getInstance().getParser(IExcelParser.class).merge(this, clone);
        return clone;
    }


    public ExcelConfiguration setFileName(String fileName) {
        this.fileName = fileName;
        this.defaultName = false;
        return this;
    }
}
