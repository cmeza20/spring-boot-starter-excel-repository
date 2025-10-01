package com.cmeza.spring.excel.repository.tests.units;

import com.cmeza.spring.excel.repository.converters.Converter;
import com.cmeza.spring.excel.repository.models.Customer;
import com.cmeza.spring.excel.repository.models.SimpleCustomer;
import com.cmeza.spring.excel.repository.support.converters.model.ToModelConverter;
import com.cmeza.spring.excel.repository.support.converters.model.ToModelMapConverter;
import com.cmeza.spring.excel.repository.support.mappers.ToModelMapper;
import com.cmeza.spring.excel.repository.support.members.EntityMember;
import com.cmeza.spring.excel.repository.support.parsers.values.ValueParser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

@Slf4j
@SpringBootTest
class ModelConverterTest {

    @BeforeAll
    static void setup() {
        log.info("Setup ModelConverterTest");
    }

    @Test
    void testModel() throws FileNotFoundException {
        ToModelConverter<Customer> converter = Converter.toModel(Customer.class);
        converter.withModelMapper(new CustomMapper());
        converter.withMapping("customerId", "ID")
                .withMapping("customerName", "NAME")
                .withMapping("customerCode", "CODE")
                .withMapping("address", "ADDRESS")
                .withMapping("employee.id", "EMP ID")
                .withMapping("employee.name", "EMP NAME")
                .withMapping("size", "SIZE")
                .withMapping("createdAt", "CREATED AT")
                .withMapping("modifiedAt", "MODIFIED AT")
                .withMapping("approbation", "APPROBATION")
                .withMapping("officeHours", "OFFICE HOURS");


        File file = ResourceUtils.getFile("classpath:customers.xlsx");

        List<Customer> results = converter.build(file);

        Assertions.assertNotNull(results, "Customers not found");
        Assertions.assertEquals(2, results.size(), "2 Customers not found");

        Customer customerCarlos = results.get(0);
        Assertions.assertEquals("C0001", customerCarlos.getCustomerCode(), "Carlos::Customer code not found");
        Assertions.assertEquals("Carlos", customerCarlos.getCustomerName(), "Carlos::Customer name not found");
        Assertions.assertEquals("Custom address", customerCarlos.getAddress(), "Carlos::Customer address not found");

        Customer customerLuis = results.get(1);
        Assertions.assertEquals(2, customerLuis.getCustomerId(), "Luis::Customer id not found");
        Assertions.assertEquals("Custom address", customerLuis.getAddress(), "Luis::Customer address not found");
    }

    @Test
    void testModelMap() throws FileNotFoundException {
        ToModelMapConverter<Customer, SimpleCustomer> converter = Converter.toModel(Customer.class, SimpleCustomer.class);
        converter.withModelMapper(new CustomMapper());
        converter.withMapping("customerId", "ID")
                .withMapping("customerName", "NAME")
                .withMapping("customerCode", "CODE")
                .withMapping("address", "ADDRESS")
                .withMapping("employee.id", "EMP ID")
                .withMapping("employee.name", "EMP NAME")
                .withMapping("size", "SIZE")
                .withMapping("createdAt", "CREATED AT")
                .withMapping("modifiedAt", "MODIFIED AT")
                .withMapping("approbation", "APPROBATION")
                .withMapping("officeHours", "OFFICE HOURS");

        converter.withMapModel((pos, customer) ->
                new SimpleCustomer()
                        .setId(customer.getCustomerId())
                        .setName(customer.getCustomerName())
                        .setCode(customer.getCustomerCode())
                        .setAddress(customer.getAddress())
                        .setApprobation(customer.getApprobation()));

        File file = ResourceUtils.getFile("classpath:customers.xlsx");

        List<SimpleCustomer> simpleCustomers = converter.buildMap(file);

        Assertions.assertNotNull(simpleCustomers, "Customers not found");
        Assertions.assertEquals(2, simpleCustomers.size(), "2 Customers not found");

        SimpleCustomer customerCarlos = simpleCustomers.get(0);
        Assertions.assertEquals("C0001", customerCarlos.getCode(), "Carlos::SimpleCustomer code not found");
        Assertions.assertEquals("Carlos", customerCarlos.getName(), "Carlos::SimpleCustomer name not found");
        Assertions.assertEquals("Custom address", customerCarlos.getAddress(), "Carlos::SimpleCustomer address not found");

        SimpleCustomer customerLuis = simpleCustomers.get(1);
        Assertions.assertEquals(2, customerLuis.getId(), "Luis::SimpleCustomer ID not found");
        Assertions.assertEquals("Custom address", customerLuis.getAddress(), "Luis::SimpleCustomer address not found");
    }

    private static class CustomMapper implements ToModelMapper<Customer> {
        @Override
        public Customer toInstance() {
            return ToModelMapper.super.toInstance();
        }

        @Override
        public Class<Customer> getClazz() {
            return ToModelMapper.super.getClazz();
        }

        @Override
        public void mapFactoryValue(EntityMember<?> entityMember, String modelAttribute, Object value, Class<?> classCast, ValueParser<?> valueParser) {
            ToModelMapper.super.mapFactoryValue(entityMember, modelAttribute, value, classCast, valueParser);
        }

        @Override
        public Customer afterFactoryMap(EntityMember<Customer> entityMember) {
            Customer customer = entityMember.getTarget();
            customer.setAddress("Custom address");
            return customer;
        }
    }


}