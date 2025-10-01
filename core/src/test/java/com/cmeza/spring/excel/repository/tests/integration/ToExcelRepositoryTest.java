package com.cmeza.spring.excel.repository.tests.integration;

import com.cmeza.spring.excel.repository.configurations.beans.SimpleToExcelMapper;
import com.cmeza.spring.excel.repository.models.Customer;
import com.cmeza.spring.excel.repository.models.Employee;
import com.cmeza.spring.excel.repository.models.Title;
import com.cmeza.spring.excel.repository.repositories.ToExcelRepository;
import com.cmeza.spring.excel.repository.utils.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@SpringBootTest
class ToExcelRepositoryTest {

    private final ToExcelRepository excelRepository;
    private static List<Employee> employeeList;
    private static List<Title> titleList;
    private static List<Customer> customerList;

    @Autowired
    public ToExcelRepositoryTest(ToExcelRepository excelRepository) {
        this.excelRepository = excelRepository;
    }

    @BeforeAll
    static void setup() {
        log.info("Setup ToExcelRepositoryTest");

        Title title = new Title().setId(1L).setName("Senior");
        employeeList = List.of(
                new Employee().setId(1L).setName("John").setLastName("Doe").setBirthDate(LocalDate.now()),
                new Employee().setId(2L).setName("Rick").setBirthDate(LocalDate.now()).setTitle(title),
                new Employee().setId(3L).setName("Ana").setLastName("Merino")
        );

        titleList = List.of(
                title,
                new Title().setId(2L).setName("Semi Senior"),
                new Title().setId(3L).setName("Junior")
        );

        customerList = List.of(
                new Customer().setCustomerId(1L).setCustomerName("Fernando").setCustomerCode("CUS001").setAddress("Address"),
                new Customer().setCustomerId(2L).setCustomerName("Luis").setCustomerCode("CUS002"),
                new Customer().setCustomerId(3L).setCustomerName("Alberto").setCustomerCode("CUS003")
        );
    }

    @Test
    void testWithAnnotationBeanMappingAndStyle() {
        Path path = excelRepository.employeeReportWithAnnotationBeansAndStyles(employeeList);

        Assertions.assertNotNull(path, "Path is null");

        try (Workbook workbook = ExcelUtils.readFromPath(path)) {
            Sheet sheetZero = workbook.getSheet("Global Report");

            Assertions.assertNotNull(sheetZero, "Sheet not found");

            Row headerRow = sheetZero.getRow(0);
            Assertions.assertNotNull(headerRow, "Header not found");

            Assertions.assertNotNull(headerRow.getCell(0), "Header ID not found");
            Assertions.assertNotNull(headerRow.getCell(1), "Header LASTNAME not found");
            Assertions.assertNotNull(headerRow.getCell(2), "Header BIRTHDATE not found");
            Assertions.assertNotNull(headerRow.getCell(3), "Header TITLE not found");

            Row rowTwo = sheetZero.getRow(2);
            Assertions.assertEquals(2d, rowTwo.getCell(0).getNumericCellValue(), "Value ID not found");
            Assertions.assertEquals("", rowTwo.getCell(1).getStringCellValue(), "Value Name not found");
            Assertions.assertEquals("Senior", rowTwo.getCell(3).getStringCellValue(), "Value Title not found");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            FileUtils.deleteQuietly(path.toFile());
        }
    }

    @Test
    void testWithAnnotationMappingAndStyle() {
        Path path = excelRepository.titleReportWithAnnotationAndStyles(titleList);

        Assertions.assertNotNull(path, "Path is null");

        try (Workbook workbook = ExcelUtils.readFromPath(path)) {
            Sheet sheetZero = workbook.getSheet("Titles");

            Assertions.assertNotNull(sheetZero, "Sheet not found");

            Row headerRow = sheetZero.getRow(0);
            Assertions.assertNotNull(headerRow, "Header not found");

            Assertions.assertNotNull(headerRow.getCell(0), "Header ID not found");
            Assertions.assertNotNull(headerRow.getCell(1), "Header NAME not found");

            Row rowTwo = sheetZero.getRow(3);
            Assertions.assertEquals(3d, rowTwo.getCell(0).getNumericCellValue(), "Value ID not found");
            Assertions.assertEquals("Junior", rowTwo.getCell(1).getStringCellValue(), "Value Name not found");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            FileUtils.deleteQuietly(path.toFile());
        }
    }

    @Test
    void testWithMappings() {
        Path path = excelRepository.customerReportWithMappings(customerList);

        Assertions.assertNotNull(path, "Path is null");

        try (Workbook workbook = ExcelUtils.readFromPath(path)) {
            Sheet sheetZero = workbook.getSheet("CUSTOMERS");

            Assertions.assertNotNull(sheetZero, "Sheet not found");

            Row headerRow = sheetZero.getRow(0);
            Assertions.assertNotNull(headerRow, "Header not found");

            Assertions.assertNotNull(headerRow.getCell(0), "Header ID not found");
            Assertions.assertNotNull(headerRow.getCell(1), "Header NAME not found");

            Row rowTwo = sheetZero.getRow(1);
            Assertions.assertEquals(1d, rowTwo.getCell(0).getNumericCellValue(), "Value ID not found");
            Assertions.assertEquals("Fernando", rowTwo.getCell(1).getStringCellValue(), "Value Name not found");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            FileUtils.deleteQuietly(path.toFile());
        }
    }

    @Test
    void testMultipleSheets() {
        Path path = excelRepository.customersAndEmployeesReport(customerList, employeeList);

        Assertions.assertNotNull(path, "Path is null");

        try (Workbook workbook = ExcelUtils.readFromPath(path)) {
            Sheet sheetZero = workbook.getSheet("Customers Report");
            Sheet sheetOne = workbook.getSheet("Custom Sheet");

            Assertions.assertNotNull(sheetZero, "Customers Sheet not found");
            Assertions.assertNotNull(sheetOne, "Custom Sheet not found");

            Row headerRow = sheetZero.getRow(0);
            Assertions.assertNotNull(headerRow, "Header not found");

            Assertions.assertNotNull(headerRow.getCell(0), "Header ID not found");
            Assertions.assertNotNull(headerRow.getCell(1), "Header NAME not found");

            Row rowTwo = sheetZero.getRow(0);
            Assertions.assertEquals("C_ID", rowTwo.getCell(0).getStringCellValue(), "Header ID not found");
            Assertions.assertEquals("C_NAME", rowTwo.getCell(1).getStringCellValue(), "Header Name not found");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            FileUtils.deleteQuietly(path.toFile());
        }
    }

    @Test
    void testWithInterceptor() {
        Path path = excelRepository.customerReportWithInterceptor(customerList);

        Assertions.assertNotNull(path, "Path is null");

        try (Workbook workbook = ExcelUtils.readFromPath(path)) {
            Sheet sheetZero = workbook.getSheet("Global Report");

            Assertions.assertNotNull(sheetZero, "Sheet not found");

            Row headerRow = sheetZero.getRow(0);
            Assertions.assertNotNull(headerRow, "Header not found");

            Row rowTwo = sheetZero.getRow(1);
            Assertions.assertEquals("100001", rowTwo.getCell(2).getStringCellValue(), "Changed Name not found");
            Assertions.assertEquals("100001", rowTwo.getCell(3).getStringCellValue(), "Changed Code not found");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            FileUtils.deleteQuietly(path.toFile());
        }
    }

    @Test
    void testWithMapper() {
        Path path = excelRepository.customerReportWithMapper(customerList);

        Assertions.assertNotNull(path, "Path is null");

        try (Workbook workbook = ExcelUtils.readFromPath(path)) {
            Sheet sheetZero = workbook.getSheet("Global Report");

            Assertions.assertNotNull(sheetZero, "Sheet not found");

            Row headerRow = sheetZero.getRow(0);
            Assertions.assertNotNull(headerRow, "Header not found");

            Row rowTwo = sheetZero.getRow(1);
            Assertions.assertEquals(SimpleToExcelMapper.NEW_ID.toString(), rowTwo.getCell(0).getStringCellValue(), "Changed ID from mapper not found");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            FileUtils.deleteQuietly(path.toFile());
        }
    }
}
