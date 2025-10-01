package com.cmeza.spring.excel.repository.tests.units;

import com.cmeza.spring.excel.repository.models.SimpleCustomer;
import com.cmeza.spring.excel.repository.builders.ExcelToModelBuilder;
import com.cmeza.spring.excel.repository.models.Customer;
import com.cmeza.spring.excel.repository.builders.ExcelToModelMapBuilder;
import com.cmeza.spring.excel.repository.templates.ExcelRepositoryTemplate;
import lombok.extern.slf4j.Slf4j;
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
class ModelBuilderTest {

    private final ExcelRepositoryTemplate excelRepositoryTemplate;

    @Autowired
    public ModelBuilderTest(ExcelRepositoryTemplate excelRepositoryTemplate) {
        this.excelRepositoryTemplate = excelRepositoryTemplate;
    }

    @BeforeAll
    static void setup() {
        log.info("Setup ModelBuilderTest");
    }

    @Test
    void test() throws FileNotFoundException {
        ExcelToModelBuilder<Customer> builder = excelRepositoryTemplate.toModel(Customer.class);
        builder.withMapping("customerId", "ID")
                .withMapping("customerName", "NAME")
                .withMapping("customerCode", "CODE")
                .withMapping("address", "ADDRESS")
                .withMapping("employee.id", "EMP ID")
                .withMapping("employee.name", "EMP NAME")
                .withMapping("size", "SIZE")
                .withMapping("createdAt", "CREATED AT")
                .withMapping("modifiedAt", "MODIFIED AT")
                .withMapping("approbation", "APPROBATION")
                .withMapping("officeHours", "OFFICE HOURS")
                .loggable(true)
                .withModelConfiguration(con -> {
                    con.setHierarchical(true);
                });

        File file = ResourceUtils.getFile("classpath:customers.xlsx");

        List<Customer> customers = builder.build(file);

        Assertions.assertNotNull(customers, "Customers not found");
        Assertions.assertEquals(2, customers.size(), "2 Customers not found");

        Customer customerCarlos = customers.get(0);
        Assertions.assertEquals("C0001", customerCarlos.getCustomerCode(), "Carlos::Customer code not found");
        Assertions.assertEquals(99, customerCarlos.getEmployee().getId(), "Carlos::Customer::Employee ID not found");

        Customer customerLuis = customers.get(1);
        Assertions.assertEquals("Ca Santiago 686", customerLuis.getAddress(), "Luis::Customer address not found");
        Assertions.assertEquals("Reporter", customerLuis.getEmployee().getName(), "Luis::Customer::Employee Name not found");
    }

    @Test
    void testMap() throws FileNotFoundException {
        ExcelToModelMapBuilder<Customer, SimpleCustomer> builder = excelRepositoryTemplate.toModel(Customer.class, SimpleCustomer.class);
        builder.withMapping("customerId", "ID")
                .withMapping("customerName", "NAME")
                .withMapping("customerCode", "CODE")
                .withMapping("address", "ADDRESS")
                .withMapping("employee.id", "EMP ID")
                .withMapping("employee.name", "EMP NAME")
                .withMapping("size", "SIZE")
                .withMapping("createdAt", "CREATED AT")
                .withMapping("modifiedAt", "MODIFIED AT")
                .withMapping("approbation", "APPROBATION")
                .withMapping("officeHours", "OFFICE HOURS")
                .loggable(true)
                .withModelConfiguration(con -> {
                    con.setHierarchical(true);
                });

        builder.withMapModel((pos, customer) ->
                new SimpleCustomer()
                        .setId(customer.getCustomerId())
                        .setName(customer.getCustomerName())
                        .setCode(customer.getCustomerCode())
                        .setAddress(customer.getAddress())
                        .setApprobation(customer.getApprobation()));

        File file = ResourceUtils.getFile("classpath:customers.xlsx");

        List<SimpleCustomer> customers = builder.buildMap(file);

        Assertions.assertNotNull(customers, "Customers not found");
        Assertions.assertEquals(2, customers.size(), "2 Customers not found");

        SimpleCustomer customerCarlos = customers.get(0);
        Assertions.assertEquals("C0001", customerCarlos.getCode(), "Carlos::SimpleCustomer code not found");
        Assertions.assertEquals("Carlos", customerCarlos.getName(), "Carlos::SimpleCustomer name not found");

        SimpleCustomer customerLuis = customers.get(1);
        Assertions.assertEquals("Ca Santiago 686", customerLuis.getAddress(), "Luis::SimpleCustomer address not found");
        Assertions.assertEquals(null, customerLuis.getEmployeeName(), "Luis::SimpleCustomer employeeName is set");
    }
}
