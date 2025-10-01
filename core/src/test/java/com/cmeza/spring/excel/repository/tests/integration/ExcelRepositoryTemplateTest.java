package com.cmeza.spring.excel.repository.tests.integration;

import com.cmeza.spring.excel.repository.annotations.TestFieldAnnotation;
import com.cmeza.spring.excel.repository.annotations.TestStyleAnnotation;
import com.cmeza.spring.excel.repository.builders.ModelToExcelBuilder;
import com.cmeza.spring.excel.repository.support.configurations.excel.FontConfiguration;
import com.cmeza.spring.excel.repository.support.configurations.excel.StyleConfiguration;
import com.cmeza.spring.excel.repository.models.Person;
import com.cmeza.spring.excel.repository.templates.ExcelRepositoryTemplate;
import com.cmeza.spring.excel.repository.utils.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@SpringBootTest
class ExcelRepositoryTemplateTest {
    public static final String HEADER_STYLE = "HEADER_STYLE";

    private static List<Person> personList;
    private static StyleConfiguration headerStyle;
    private final ExcelRepositoryTemplate excelRepositoryTemplate;

    @Autowired
    public ExcelRepositoryTemplateTest(ExcelRepositoryTemplate excelRepositoryTemplate) {
        this.excelRepositoryTemplate = excelRepositoryTemplate;
    }

    @BeforeAll
    static void setup() {
        log.info("Setup ExcelRepositoryTemplateTest");

        personList = List.of(
                new Person().setId(1L).setName("John").setLastName("Doe"),
                new Person().setId(2L).setName("Rick"),
                new Person().setId(3L).setName("Ana").setLastName("Merino")
        );

        headerStyle = new StyleConfiguration()
                .setAlias(HEADER_STYLE)
                .setFillBackgroundColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex())
                .setFillPatternType(FillPatternType.SOLID_FOREGROUND)
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.CENTER)
                .setFont(new FontConfiguration()
                        .setBold(true)
                        .setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex()));
    }

    @Test
    void toExcelRepositoryWithoutAnnotationsTest() {
        log.info("Init toExcelRepositoryWithoutAnnotationsTest");

        final String sheetName = "My Sheet # 1";

        ModelToExcelBuilder excel = excelRepositoryTemplate.toExcel();
        excel.withKey("ManualRepositoryTest::toExcelRepositoryWithoutAnnotationsTest");
        excel.sheet()
                .withHeader(excelHeaderConverter -> {
                    excelHeaderConverter.setHeader(true);
                    excelHeaderConverter.setHeightMultiplier(2);
                })
                .withSheetName(sheetName)
                .withData(personList);

        Path result = excel.buildFile();
        Assertions.assertNotNull(result, String.format("Excel path not found: %s", result));

        try (Workbook workbook = ExcelUtils.readFromPath(result)) {
            Sheet sheetZero = workbook.getSheet(sheetName);

            Assertions.assertNotNull(sheetZero, "Sheet not found");

            Row headerRow = sheetZero.getRow(0);
            Assertions.assertNotNull(headerRow, "Header not found");

            Assertions.assertNotNull(headerRow.getCell(0), "Header ID not found");
            Assertions.assertNotNull(headerRow.getCell(1), "Header NAME not found");
            Assertions.assertNotNull(headerRow.getCell(2), "Header LASTNAME not found");

            Row rowTwo = sheetZero.getRow(2);
            Assertions.assertEquals(2d, rowTwo.getCell(0).getNumericCellValue(), "Value ID not found");
            Assertions.assertEquals("Rick", rowTwo.getCell(1).getStringCellValue(), "Value Name not found");
            Assertions.assertEquals("", rowTwo.getCell(2).getStringCellValue(), "Value LastName not found");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            FileUtils.deleteQuietly(result.toFile());
        }
    }

    @Test
    void toExcelRepositoryWithMappingTest() {
        log.info("Init toExcelRepositoryWithMappingTest");

        final String sheetName = "My Sheet with Mapping # 1";

        ModelToExcelBuilder excel = excelRepositoryTemplate.toExcel();
        excel.withKey("ManualRepositoryTest::toExcelRepositoryWithMappingTest");
        excel.sheet()
                .withHeader(excelHeaderConverter -> {
                    excelHeaderConverter.setHeader(true);
                    excelHeaderConverter.setHeightMultiplier(2);
                })
                .withSheetName(sheetName)
                .withMapping("id", "PERSON ID")
                .withMapping("name", "PERSON NAME")
                .withMapping("lastName", "PERSON LASTNAME")
                .withData(personList);

        Path result = excel.buildFile();
        Assertions.assertNotNull(result, String.format("Excel path not found: %s", result));

        try (Workbook workbook = ExcelUtils.readFromPath(result)) {
            Sheet sheetZero = workbook.getSheet(sheetName);

            Assertions.assertNotNull(sheetZero, "Sheet not found");

            Row headerRow = sheetZero.getRow(0);
            Assertions.assertNotNull(headerRow, "Header not found");

            Assertions.assertEquals("PERSON ID", headerRow.getCell(0).getStringCellValue(), "PERSON ID not found");
            Assertions.assertEquals("PERSON NAME", headerRow.getCell(1).getStringCellValue(), "PERSON NAME not found");
            Assertions.assertEquals("PERSON LASTNAME", headerRow.getCell(2).getStringCellValue(), "PERSON LASTNAME not found");

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            FileUtils.deleteQuietly(result.toFile());
        }
    }

    @Test
    void toExcelRepositoryWithStyleTest() {
        log.info("Init toExcelRepositoryWithStyleTest");

        final String sheetName = "My Sheet with Style # 1";

        ModelToExcelBuilder excel = excelRepositoryTemplate.toExcel();
        excel.withKey("ManualRepositoryTest::toExcelWithStyleTest");
        excel.sheet()
                .withSheetConfiguration(config -> {
                    config.addStyle(headerStyle);
                })
                .withHeader(excelHeaderConverter -> {
                    excelHeaderConverter.setHeader(true);
                    excelHeaderConverter.setHeightMultiplier(2);
                    excelHeaderConverter.setStyleAlias(HEADER_STYLE);
                })
                .withSheetName(sheetName)
                .withData(personList);

        Path result = excel.buildFile();
        Assertions.assertNotNull(result, String.format("Excel path not found: %s", result));

        try (Workbook workbook = ExcelUtils.readFromPath(result)) {
            Sheet sheetZero = workbook.getSheet(sheetName);

            Assertions.assertNotNull(sheetZero, "Sheet not found");

            Row headerRow = sheetZero.getRow(0);
            Assertions.assertNotNull(headerRow, "Header not found");

            XSSFCellStyle cellStyle = (XSSFCellStyle) headerRow.getCell(0).getCellStyle();
            Assertions.assertNotNull(cellStyle, "CellStyle not found");

            Assertions.assertEquals(HorizontalAlignment.CENTER, cellStyle.getAlignment(), "HorizontalAlignment.CENTER not found");
            Assertions.assertEquals(VerticalAlignment.CENTER, cellStyle.getVerticalAlignment(), "VerticalAlignment.CENTER not found");
            Assertions.assertTrue(cellStyle.getFont().getBold(), "Font.BOLD not found");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            FileUtils.deleteQuietly(result.toFile());
        }
    }

    @Test
    void toExcelRepositoryWithAnnotationTest() {
        log.info("Init toExcelRepositoryWithAnnotationTest");

        final String sheetName = "My Sheet with Annotation # 1";

        ModelToExcelBuilder excel = excelRepositoryTemplate.toExcel();
        excel.withKey("ManualRepositoryTest::toExcelRepositoryWithAnnotationTest");
        excel.sheet()
                .withSheetConfiguration(config -> {
                    config.addStyle(headerStyle);
                })
                .withHeader(excelHeaderConverter -> {
                    excelHeaderConverter.setHeader(true);
                    excelHeaderConverter.setHeightMultiplier(2);
                    excelHeaderConverter.setStyleAlias(HEADER_STYLE);
                })
                .withAutoSize(true)
                .withFieldAnnotation(TestStyleAnnotation.class, (testStyleAnnotation, fieldConfiguration) -> {
                    if (!fieldConfiguration.isHeader()) {
                        return;
                    }

                    if (StringUtils.isNotEmpty(testStyleAnnotation.styleAlias())) {
                        fieldConfiguration.setStyleAlias(testStyleAnnotation.styleAlias());
                    }

                    if (testStyleAnnotation.bold()) {
                        fieldConfiguration.getStyle().getFont().setBold(true);
                    }
                    if (testStyleAnnotation.color() != 0) {
                        fieldConfiguration.getStyle().getFont().setColor(testStyleAnnotation.color());
                    }

                    if (testStyleAnnotation.italic()) {
                        fieldConfiguration.getStyle().getFont().setItalic(true);
                    }

                    if (testStyleAnnotation.rowPosition().length > 0) {
                        fieldConfiguration.setStyleRowPosition(testStyleAnnotation.rowPosition());
                    }
                })
                .withFieldAnnotation(TestFieldAnnotation.class, (testFieldAnnotation, fieldConfiguration) -> {
                    if (StringUtils.isNotEmpty(testFieldAnnotation.header())) {
                        fieldConfiguration.setHeaderName(testFieldAnnotation.header());
                    }
                })
                .withSheetName(sheetName)
                .preBuilt(Person.class)
                .withData(personList);

        Path result = excel.buildFile();
        Assertions.assertNotNull(result, String.format("Excel path not found: %s", result));

        try (Workbook workbook = ExcelUtils.readFromPath(result)) {
            Sheet sheetZero = workbook.getSheet(sheetName);

            Assertions.assertNotNull(sheetZero, "Sheet not found");

            Row headerRow = sheetZero.getRow(0);
            Assertions.assertNotNull(headerRow, "Header not found");

            XSSFCellStyle cellStyle = (XSSFCellStyle) headerRow.getCell(0).getCellStyle();
            Assertions.assertNotNull(cellStyle, "CellStyle not found");

            Assertions.assertEquals(HorizontalAlignment.CENTER, cellStyle.getAlignment(), "HorizontalAlignment.CENTER not found");
            Assertions.assertEquals(VerticalAlignment.CENTER, cellStyle.getVerticalAlignment(), "VerticalAlignment.CENTER not found");
            Assertions.assertTrue(cellStyle.getFont().getBold(), "Font.BOLD not found");

            XSSFCellStyle customCellStyle = (XSSFCellStyle) headerRow.getCell(2).getCellStyle();
            Assertions.assertNotNull(customCellStyle, "Custom CellStyle not found");

            Assertions.assertTrue(customCellStyle.getFont().getBold(), "Custom Font.BOLD not found");
            Assertions.assertTrue(customCellStyle.getFont().getItalic(), "Custom Font.ITALIC not found");
            Assertions.assertEquals(25, customCellStyle.getFont().getColor(), "Custom Font.COLOR not found");

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            FileUtils.deleteQuietly(result.toFile());
        }
    }

}
