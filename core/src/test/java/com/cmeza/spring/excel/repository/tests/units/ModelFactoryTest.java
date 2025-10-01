package com.cmeza.spring.excel.repository.tests.units;

import com.cmeza.spring.excel.repository.factories.Factory;
import com.cmeza.spring.excel.repository.models.SimpleCustomer;
import com.cmeza.spring.excel.repository.support.factories.model.ModelFactory;
import com.cmeza.spring.excel.repository.models.Customer;
import com.cmeza.spring.excel.repository.support.factories.model.ModelMapFactory;
import com.cmeza.spring.excel.repository.support.mappers.ToModelMapper;
import com.cmeza.spring.excel.repository.support.members.EntityMember;
import com.cmeza.spring.excel.repository.support.parsers.values.ValueParser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
class ModelFactoryTest {

    @BeforeAll
    static void setup() {
        log.info("Setup ModelFactoryTest");
    }

    @Test
    void test() {
        ModelFactory<Customer> customerModelFactory = Factory.getModelFactory(Customer.class);
        customerModelFactory.withHierarchical(true);
        customerModelFactory.withModelMapper(new CustomCustomerMapper());
        customerModelFactory.item()
                .withValue("customerId", 1L)
                .withValue("customerName", "Carlos")
                .withValue("customerCode", "C0001")
                .withValue("address", "Ca Santa Rosa 5686")
                .withValue("employee.title.id", "99")
                .withValue("employee.title.name", "Admin");

        customerModelFactory.item()
                .withValue("customerId", "2", Long.class)
                .withValue("customerName", "Luis")
                .withValue("customerCode", "C0002", String.class)
                .withValue("address", "Ca Santiago 686", String.class)
                .withValue("employee.title.id", "98")
                .withValue("employee.title.name", "Reporter");

        List<Customer> customers = customerModelFactory.build();

        Assertions.assertNotNull(customers, "Customers not found");
        Assertions.assertEquals(2, customers.size(), "2 Customers not found");

        Customer customerCarlos = customers.get(0);
        Assertions.assertEquals("C0001", customerCarlos.getCustomerCode(), "Carlos::Customer code not found");
        Assertions.assertEquals(99, customerCarlos.getEmployee().getTitle().getId(), "Carlos::Customer::Employee::Title ID not found");

        Customer customerLuis = customers.get(1);
        Assertions.assertEquals("Ca Santiago 686", customerLuis.getAddress(), "Luis::Customer address not found");
        Assertions.assertEquals("Reporter", customerLuis.getEmployee().getTitle().getName(), "Luis::Customer::Employee::Title Name not found");

        Assertions.assertEquals("CUSTOM EMPLOYEE NAME", customerCarlos.getEmployee().getName(), "Carlos::Customer custom name not found");
        Assertions.assertEquals("CUSTOM EMPLOYEE NAME", customerLuis.getEmployee().getName(), "Luis::Customer custom name not found");
    }

    @Test
    void testMap() {
        ModelMapFactory<Customer, SimpleCustomer> customerModelFactory = Factory.getModelFactory(Customer.class, SimpleCustomer.class);
        customerModelFactory.withHierarchical(true);
        customerModelFactory.withModelMapper(new CustomCustomerMapper());
        customerModelFactory.item()
                .withValue("customerId", 1L)
                .withValue("customerName", "Carlos")
                .withValue("customerCode", "C0001")
                .withValue("address", "Ca Santa Rosa 5686")
                .withValue("employee.title.id", "99")
                .withValue("employee.title.name", "Admin");

        customerModelFactory.item()
                .withValue("customerId", "2", Long.class)
                .withValue("customerName", "Luis")
                .withValue("customerCode", "C0002", String.class)
                .withValue("address", "Ca Santiago 686", String.class)
                .withValue("employee.title.id", "98")
                .withValue("employee.title.name", "Reporter");

        customerModelFactory.withMapModel((pos, customer) ->
                new SimpleCustomer()
                        .setId(customer.getCustomerId())
                        .setName(customer.getCustomerName())
                        .setCode(customer.getCustomerCode())
                        .setAddress(customer.getAddress())
                        .setApprobation(customer.getApprobation())
                        .setEmployeeName(customer.getEmployee().getName()));

        List<SimpleCustomer> customers = customerModelFactory.buildMap();

        Assertions.assertNotNull(customers, "Customers not found");
        Assertions.assertEquals(2, customers.size(), "2 Customers not found");

        SimpleCustomer customerCarlos = customers.get(0);
        Assertions.assertEquals("C0001", customerCarlos.getCode(), "Carlos::SimpleCustomer code not found");
        Assertions.assertEquals("Carlos", customerCarlos.getName(), "Carlos::SimpleCustomer name not found");

        SimpleCustomer customerLuis = customers.get(1);
        Assertions.assertEquals("Ca Santiago 686", customerLuis.getAddress(), "Luis::SimpleCustomer address not found");
        Assertions.assertEquals("C0002", customerLuis.getCode(), "Luis::SimpleCustomer code not found");

        Assertions.assertEquals("CUSTOM EMPLOYEE NAME", customerCarlos.getEmployeeName(), "Carlos::SimpleCustomer custom employeeName not found");
        Assertions.assertEquals("CUSTOM EMPLOYEE NAME", customerLuis.getEmployeeName(), "Luis::SimpleCustomer custom employeeName not found");
    }

    private static class CustomCustomerMapper implements ToModelMapper<Customer> {
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
            customer.getEmployee().setName("CUSTOM EMPLOYEE NAME");
            return customer;
        }
    }

}