package com.cmeza.spring.excel.repository.tests.integration;

import com.cmeza.spring.excel.repository.models.Customer;
import com.cmeza.spring.excel.repository.models.Employee;
import com.cmeza.spring.excel.repository.models.SimpleCustomer;
import com.cmeza.spring.excel.repository.repositories.ToModelRepository;
import com.cmeza.spring.excel.repository.support.exceptions.ExcelException;
import com.cmeza.spring.excel.repository.support.results.ExcelValidated;
import com.cmeza.spring.excel.repository.support.results.MapValidated;
import com.cmeza.spring.excel.repository.support.results.Validated;
import com.cmeza.spring.excel.repository.support.results.ValidatedError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

@Slf4j
@SpringBootTest
class ToModelRepositoryTest {

    private final ToModelRepository modelRepository;
    private static File customersFile;
    private static File employeesFile;
    private static File employeesErrorFile;

    @Autowired
    public ToModelRepositoryTest(ToModelRepository modelRepository) {
        this.modelRepository = modelRepository;
    }

    @BeforeAll
    static void setup() throws FileNotFoundException {
        log.info("Setup ToModelRepositoryTest");
        employeesFile = ResourceUtils.getFile("classpath:employees.xlsx");
        employeesErrorFile = ResourceUtils.getFile("classpath:employees-error.xlsx");
        customersFile = ResourceUtils.getFile("classpath:customers.xlsx");
    }

    @Test
    void simpleReadEmployeesTest() {
        List<Employee> employees = modelRepository.readEmployees(employeesFile);
        Assertions.assertEquals(2, employees.size());

        Employee firstEmployee = employees.get(0);
        Assertions.assertNotNull(firstEmployee, "First employee is null");
        Assertions.assertEquals(800, firstEmployee.getId(), "ID does not match");
        Assertions.assertEquals("Senior", firstEmployee.getTitle().getName(), "Title name does not match");

        Employee secondEmployee = employees.get(1);
        Assertions.assertNotNull(secondEmployee, "Second employee is null");
        Assertions.assertEquals(200, secondEmployee.getTitle().getId(), "Title id does not match");
        Assertions.assertEquals("lastName", secondEmployee.getLastName(), "Validated lastname does not match");
    }

    @Test
    void validationRedEmployeesTest() {
        Assertions.assertThrowsExactly(ExcelException.class, () -> modelRepository.readEmployees(employeesErrorFile));
    }

    @Test
    void validateEmployeesWithModelMapper() {
        Validated<Employee> employeeValidated = modelRepository.validatedEmployeesWithModelMapper(employeesErrorFile);
        Assertions.assertNotNull(employeeValidated, "Employee validated is null");
        Assertions.assertEquals(1, employeeValidated.getErrors().size(), "Employee validated error not found");
        Assertions.assertEquals("Constraint does not match, Must be greater than 200", employeeValidated.getErrors().get(0).getError(), "Employee validated error not found");
    }

    @Test
    void validateErrorEmployeesWithModelMapper() {
        ValidatedError<Employee> employeeValidatedError = modelRepository.validatedErrorEmployeesWithModelMapper(employeesErrorFile);
        Assertions.assertNotNull(employeeValidatedError, "Employee validated error is null");
        Assertions.assertEquals(1, employeeValidatedError.getErrors().size(), "Employee validated error not found");
        Assertions.assertEquals(1, employeeValidatedError.getAll().size(), "Employee all rows not found");
        Assertions.assertEquals("Constraint does not match, Must be greater than 200", employeeValidatedError.getErrors().get(0).getError(), "Employee validated error not found");
    }

    @Test
    void validateCustomerWithMapTransform() {
        MapValidated<Customer, SimpleCustomer> customerMapValidated = modelRepository.validatedWithMapTransform(customersFile);

        Assertions.assertNotNull(customerMapValidated, "Customers validated error is null");
        Assertions.assertEquals(0, customerMapValidated.getErrors().size(), "Customers validated has found");
        Assertions.assertEquals(2, customerMapValidated.getMapList().size(), "Customers Map rows not found");

        SimpleCustomer customer = customerMapValidated.getMapList().get(0);
        Assertions.assertEquals("Admin", customer.getEmployeeName(), "Customer :: employee name does not match");
        Assertions.assertEquals("C0001", customer.getCode(), "Customer :: code does not match");

    }

    @Test
    void validateCustomerWithExcelError() {
        ExcelValidated<Employee> employeeExcelValidated = modelRepository.validatedWithExcelError(employeesErrorFile);

        File file = employeeExcelValidated.getFileError();

        Assertions.assertNotNull(file, "Employee file error is null");

        Assertions.assertEquals("validatedWithExcelError.xlsx", file.getName(), "File error name does not match");

        FileUtils.deleteQuietly(file);
    }

    @Test
    void validateCustomerWithExcelErrorDsl() {
        ExcelValidated<Employee> employeeExcelValidated = modelRepository.validatedWithExcelErrorDsl(employeesErrorFile);

        File file = employeeExcelValidated.getFileError();

        Assertions.assertNotNull(file, "Employee file error is null");

        Assertions.assertEquals("validatedWithExcelError.xlsx", file.getName(), "File error name does not match");

        FileUtils.deleteQuietly(file);
    }

}
