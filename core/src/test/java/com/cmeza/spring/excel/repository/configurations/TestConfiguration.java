package com.cmeza.spring.excel.repository.configurations;

import com.cmeza.spring.excel.repository.support.configurations.excel.*;
import com.cmeza.spring.excel.repository.support.enums.TableStyleValue;
import org.apache.poi.ss.usermodel.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfiguration {

    @Bean
    public ExcelConfiguration globalExcelConfiguration() {
        return new ExcelConfiguration();
    }

    @Bean
    public SheetConfiguration globalSheetConfiguration() {
        return new SheetConfiguration()
                .setSheetName("Global Report");
    }

    @Bean
    public StyleConfiguration globalStyleConfiguration() {
        return new StyleConfiguration()
                .setAlias("GLOBAL_STYLE")
                .withFont(fontConfiguration -> {
                    fontConfiguration.setBold(true);
                    fontConfiguration.setItalic(true);
                });
    }

    @Bean
    public HeaderConfiguration globalHeaderConfiguration() {
        return new HeaderConfiguration()
                .setFreeze(true)
                .setHeader(true)
                .setHeightMultiplier(2)
                .setStyleAlias("HEADER_STYLE");
    }

    @Bean
    public StyleConfiguration customColumnStyle() {
        return new StyleConfiguration()
                .setAlias("CUSTOM_COLUMN_STYLE")
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.CENTER)
                .setFillForegroundColor(IndexedColors.YELLOW.getIndex())
                .setFillPatternType(FillPatternType.SOLID_FOREGROUND)
                .withFont(fontConfiguration -> {
                    fontConfiguration.setBold(true);
                    fontConfiguration.setColor(IndexedColors.GREEN.getIndex());
                });
    }

    @Bean
    public SheetConfiguration customSheetConfiguration(HeaderConfiguration globalHeaderConfiguration) {
        return new SheetConfiguration()
                .setSheetName("Custom Sheet")
                .setAutoSize(true)
                .setHeader(globalHeaderConfiguration)
                .setTable(customTableConfiguration());
    }

    @Bean
    public TableConfiguration customTableConfiguration() {
        return new TableConfiguration()
                .setTable(true)
                .setFilter(true)
                .setStyle(TableStyleValue.TABLE_STYLE_MEDIUM_12);
    }

    @Bean
    public StyleConfiguration customHeaderStyleConfiguration() {
        return new StyleConfiguration()
                .setAlias("HEADER_STYLE")
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.CENTER)
                .setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex())
                .setFillPatternType(FillPatternType.SOLID_FOREGROUND)
                .withFont(fontConfiguration -> {
                    fontConfiguration.setBold(true);
                    fontConfiguration.setColor(IndexedColors.WHITE.getIndex());
                });
    }

    @Bean
    public SheetConfiguration customerSheetConfiguration() {
        return new SheetConfiguration()
                .setSheetName("Customers Report")
                .setAutoSize(true)
                .addStyle(new StyleConfiguration()
                        .setAlias("CUSTOMER_ID_STYLE")
                        .setHorizontalAlignment(HorizontalAlignment.CENTER)
                        .setFont(new FontConfiguration()
                                .setBold(true)
                                .setItalic(true)
                                .setUnderline(FontUnderline.DOUBLE)
                                .setColor(IndexedColors.YELLOW.getIndex())))
                .setTable(new TableConfiguration()
                        .setTable(true)
                        .setFilter(false)
                        .setStyle(TableStyleValue.TABLE_STYLE_DARK_4))
                .setHeader(new HeaderConfiguration()
                        .setHeader(true)
                        .setFreeze(false)
                        .setHeightMultiplier(2))
                .addMapping("customerId", "C_ID", "CUSTOMER_ID_STYLE")
                .addMapping("customerName", "C_NAME")
                .addMapping("address", "C_ADDRESS");
    }

}
