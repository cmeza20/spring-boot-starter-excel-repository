# Spring Boot Starter Excel Repository [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.cmeza/spring-boot-starter-excel-repository/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.cmeza/spring-boot-starter-excel-repository)

Excel repositories interfaces

### Wiki ##

* [Get Started](https://github.com/cmeza20/spring-boot-starter-excel-repository/wiki/Get-Started)
* [Properties](https://github.com/cmeza20/spring-boot-starter-excel-repository/wiki/Properties)
* [@ExcelRepository annotation](https://github.com/cmeza20/spring-boot-starter-excel-repository/wiki/@ExcelRepository)

#### Method annotations
* [@ToExcel annotation](https://github.com/cmeza20/spring-boot-starter-excel-repository/wiki/@ToExcel-annotation)
* [@ToModel annotation](https://github.com/cmeza20/spring-boot-starter-excel-repository/wiki/@ToModel-annotation)
***

#### Model annotations
* [@Column annotation](https://github.com/cmeza20/spring-boot-starter-excel-repository/wiki/@Column-annotation)
* [@Sheet annotation](https://github.com/cmeza20/spring-boot-starter-excel-repository/wiki/@Sheet-annotation)
* [@Style annotation](https://github.com/cmeza20/spring-boot-starter-excel-repository/wiki/@Style-annotation)
***

#### Support annotations
* [@Error annotation](https://github.com/cmeza20/spring-boot-starter-excel-repository/wiki/@Error-annotation)
* [@Font annotation](https://github.com/cmeza20/spring-boot-starter-excel-repository/wiki/@Font-annotation)
* [@Header annotation](https://github.com/cmeza20/spring-boot-starter-excel-repository/wiki/@Header-annotation)
* [@Mapping annotation](https://github.com/cmeza20/spring-boot-starter-excel-repository/wiki/@Mapping-annotation)
* [@Table annotation](https://github.com/cmeza20/spring-boot-starter-excel-repository/wiki/@Table-annotation)
***

#### Advanced
* [RepositoryTemplate](https://github.com/cmeza20/spring-boot-starter-excel-repository/wiki/Builders)
* [Converters](https://github.com/cmeza20/spring-boot-starter-excel-repository/wiki/Converters)
* [Factories](https://github.com/cmeza20/spring-boot-starter-excel-repository/wiki/Factories)
* [Aware](https://github.com/cmeza20/spring-boot-starter-excel-repository/wiki/Aware)
* [PropertyResolver](https://github.com/cmeza20/spring-boot-starter-excel-repository/wiki/PropertyResolver)
***

## Maven Integration ##

```xml
<dependency>
    <groupId>com.cmeza</groupId>
    <artifactId>spring-boot-starter-excel-repository</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Minimal dependencies ##
- @EnableExcelRepositories annotation

```java
@EnableExcelRepositories
@SpringBootApplication
public class SpringBootStarterExcelRepositoryTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootStarterExcelRepositoryTestApplication.class, args);
    }
}
```

## Example ##
    Properties
    Global bean: available for use with @ToExcel
    DSL: available for use with @ToModel

```yaml
spring:
  excel:
    repository:
      global-sheet-configuration-bean: globalSheetConfiguration
      dsl:
        to-model:
          validatedWithExcelErrorDsl:
            mapping:
              - field-name: title.id
                header-name: title_id
            mapper: com.cmeza.spring.excel.repository.configurations.beans.EmployeeToModelMapper
            error:
              file-name: validatedWithExcelError.xlsx
              versioned: false

```

    Model to excel 

```java
@ExcelRepository
public interface ToExcelRepository {
    
    //Simple conversion, where the configurations are native or by annotations in the model
    @ToExcel
    Path convertToExcel(List<Employee> employeeList);

    //Conversion where the configuration comes from a bean
    @Sheet("customSheetConfiguration")
    @ToExcel(versioned = true, prefix = "TIT-")
    Path convertToExcelWithSheetConfiguration(List<Title> employeeList);

    //Conversion where column mapping is explicit
    @ToExcel
    @Sheet(name = "CUSTOMERS", mappings = {
            @Mapping(value = "customerId", headerName = "ID"),
            @Mapping(value = "customerName", headerName = "NAME"),
    })
    Path convertToExcelWithMappings(List<Customer> customerList);

    //Conversion with two sheets and bean configurations
    @ToExcel
    @Sheet("customerSheetConfiguration")
    @Sheet("employeeSheetConfiguration")
    Path convertToExcelWithMultipleSheets(List<Customer> customerList, List<Employee> employeeList);

    //Conversion with an interceptor
    @ToExcel(interceptor = LogToExcelInterceptor.class)
    Path convertToExcelWithInterceptor(List<Customer> customerList);

    //Conversion with a mapper
    @ToExcel(mapper = SimpleToExcelMapper.class)
    Path convertToExcelWithMapper(List<Customer> customerList);
}
```

    Excel to Model

```java
@ExcelRepository
public interface ToModelRepository {

    //Reading Excel with custom mapping
    @ToModel(mappings = @Mapping(value = "title.id", headerName = "title_id"))
    List<Employee> readEmployees(File file);

    //Reading and validating Excel with a custom model mapper
    @ToModel(mappings = @Mapping(value = "title.id", headerName = "title_id"), mapper = EmployeeToModelMapper.class)
    Validated<Employee> validatedEmployeesWithModelMapper(File file);

    //Reading and validating Excel with a custom model mapper (successful result not available)
    @ToModel(mappings = @Mapping(value = "title.id", headerName = "title_id"), mapper = EmployeeToModelMapper.class)
    ValidatedError<Employee> validatedErrorEmployeesWithModelMapper(File file);

    //Reading and validating Excel with a custom model mapper and mappings
    //Convert back to another model
    @ToModel(mappings = {
            @Mapping(value = "customerId", headerName = "ID"),
            @Mapping(value = "customerName", headerName = "NAME"),
            @Mapping(value = "customerCode", headerName = "CODE"),
            @Mapping(value = "address", headerName = "ADDRESS"),
            @Mapping(value = "employee.id", headerName = "EMP ID"),
            @Mapping(value = "employee.name", headerName = "EMP NAME"),
            @Mapping(value = "size", headerName = "SIZE"),
            @Mapping(value = "createdAt", headerName = "CREATED AT"),
            @Mapping(value = "modifiedAt", headerName = "MODIFIED AT"),
            @Mapping(value = "approbation", headerName = "APPROBATION"),
            @Mapping(value = "officeHours", headerName = "OFFICE HOURS"),
    }, mapper = CustomerToModelMapper.class, map = CustomerMapModel.class)
    MapValidated<Customer, SimpleCustomer> validatedWithMapTransform(File file);

    //Reading and validating Excel with a custom model mapper (Similar to Validated with an Excel return error)
    @ToModel(mappings = @Mapping(value = "title.id", headerName = "title_id"),
            mapper = EmployeeToModelMapper.class,
            error = @Error(fileName = "validatedWithExcelError.xlsx", versioned = false))
    ExcelValidated<Employee> validatedWithExcelError(File file);


    //Reading and validating Excel with DSL (properties configuration)
    @ToModel
    ExcelValidated<Employee> validatedWithExcelErrorDsl(File file);
}
```

    MODEL
    Javax or jakarta annotation supports, available for use with @ToModel
    @Column: available for use in @ToModel or @ToExcel annotation

```java
@Data
public class Employee {
    
    //Javax/Jakarta validations
    @Min(value = 200, message = "Must be greater than 200")
    @Max(value = 1000, message = "Must be less than 1000")
    @Column("identification")
    private Long id;

    @Column(ignored = true)
    private String name;

    @Equals("lastName")
    @Column(value = "last_name", styleName = "CUSTOM_COLUMN_STYLE")
    private String lastName;

    @Column(value = "birth_date")
    private LocalDate birthDate;

    @Column(mapping = "title.name", header = "title_name")
    private Title title;

    @Column
    private String error;
}
```
    Model
    @Sheet: available for use with @ToExcel
    @Style: available for use with @ToExcel

```java
//@Sheet and @Style available
@Data
@Sheet(name = "Titles")
@Style(name = "customHeaderStyleConfiguration")
public class Title {
    private Long id;
    private String name;

    public Title setId(Long id) {
        this.id = id;
        return this;
    }

    public Title setName(String name) {
        this.name = name;
        return this;
    }
}
```

    @Beans configurations: available for use with @ToExcel

```java
@Configuration
public class ToExcelConfiguration {

    //Global configuration
    @Bean
    public ExcelConfiguration globalExcelConfiguration() {
        return new ExcelConfiguration().setPath(Path.of("D://TEMP"));
    }

    //Custom sheet name configuration
    @Bean
    public SheetConfiguration globalSheetConfiguration() {
        return new SheetConfiguration()
                .setSheetName("Global Report");
    }

    //Custom style configuration with alias
    @Bean
    public StyleConfiguration globalStyleConfiguration() {
        return new StyleConfiguration()
                .setAlias("GLOBAL_STYLE")
                .withFont(fontConfiguration -> {
                    fontConfiguration.setBold(true);
                    fontConfiguration.setItalic(true);
                });
    }

    //Excel header configuration, link to StyleConfiguration
    @Bean
    public HeaderConfiguration globalHeaderConfiguration() {
        return new HeaderConfiguration()
                .setFreeze(true)
                .setHeader(true)
                .setHeightMultiplier(2)
                .setStyleAlias("HEADER_STYLE");
    }

    //Custom StyleConfiguration
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

    //Custom Sheet configuration with inherit header configuration
    @Bean
    public SheetConfiguration customSheetConfiguration(HeaderConfiguration globalHeaderConfiguration) {
        return new SheetConfiguration()
                .setSheetName("Custom Sheet")
                .setAutoSize(true)
                .setHeader(globalHeaderConfiguration)
                .setTable(customTableConfiguration());
    }

    //Custom table configuration
    @Bean
    public TableConfiguration customTableConfiguration() {
        return new TableConfiguration()
                .setTable(true)
                .setFilter(true)
                .setStyle(TableStyleValue.TABLE_STYLE_MEDIUM_12);
    }

    //Custom style configuration
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

    //Complex Sheet configuration
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
```
    ToExcelInterceptor: intercepts the conversion flow, available for use with @ToExcel

```java
public class LogToExcelInterceptor implements ToExcelInterceptor {
    @Override
    public void beforeRow(SXSSFSheet sheet, HeaderFactory headerFactory) {
    log.info("beforeRow HEADER :: Sheet: [{}]", sheet.getSheetName());
    }

    @Override
    public void beforeRow(SXSSFSheet sheet, RowFactory rowFactory, int rowPosition) {
        log.info("beforeRow ROW :: Sheet: [{}] Row Position: [{}]", sheet.getSheetName(), rowPosition);
    }

    @Override
    public void afterRow(SXSSFSheet sheet, SXSSFRow row, CellStyle cellStyle, int rowPosition, boolean isHeader) {
        log.info("afterRow :: Sheet: [{}] Row Position: [{}] isHeader: [{}]", sheet.getSheetName(), rowPosition, isHeader);
    }

    @Override
    public void beforeCell(SXSSFSheet sheet, SXSSFRow row, CellFactory cellFactory, int rowPosition, int colPosition) {
        log.info("beforeCell :: Sheet: [{}] Row Position: [{}] Col Position: {}]", sheet.getSheetName(), rowPosition, colPosition);
    }

    @Override
    public <T> void afterCell(SXSSFSheet sheet, SXSSFRow row, SXSSFCell cell, ValueParser<T> valueParser, Object value) {
        log.info("afterCell :: Sheet: [{}] Row Position: [{}] Col Position: [{}] Value: [{}]", sheet.getSheetName(), cell.getRowIndex(), cell.getColumnIndex(), value);
    }
}
```

    ToExcelMapper: Model to Cell  Mapper, available for use with @ToExcel

```java
public class SimpleToExcelMapper implements ToExcelMapper {
    
    @Override
    public CellStyle cellStyleMapper(SXSSFWorkbook workbook, SXSSFSheet sheet, SXSSFRow row, SXSSFCell cell, CellStyle cellStyle) {
        //Modify the style for each cell
        return cellStyle;
    }

    @Override
    public ValueParser<?> cellValueParserMapper(SXSSFWorkbook workbook, SXSSFSheet sheet, SXSSFRow row, SXSSFCell cell, Map<Class<?>, ValueParser<?>> valueParsers, ValueParser<?> valueParser) {
        //Executes or modifies the result value of ValueParser
        return valueParser;
    }

    @Override
    public Object cellValueMapper(SXSSFWorkbook workbook, SXSSFSheet sheet, SXSSFRow row, SXSSFCell cell, Object value) {
        //Modify the value, after cellValueParserMapper
        return value;
    }
    
}
```

    MapModel: Available for use with @ToModel

```java
@Slf4j
@Component
public class CustomerMapModel implements MapModel<Customer, SimpleCustomer> {

    @Override
    public SimpleCustomer map(int position, Customer customer) {
        return new SimpleCustomer()
                .setId(customer.getCustomerId())
                .setName(customer.getCustomerName())
                .setCode(customer.getCustomerCode())
                .setAddress(customer.getAddress())
                .setApprobation(customer.getApprobation())
                .setEmployeeName(customer.getEmployee().getName());
    }
}
```

License
----

MIT
