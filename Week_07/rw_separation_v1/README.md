# 0 Source Code

[rw-separation-v1](https://github.com/zhongmingwu/java-training-camp/tree/main/week07/rw-separation-v1)

# 1 Files

```
$ tree
.
├── HELP.md
├── mvnw
├── mvnw.cmd
├── pom.xml
├── rw-separation-v1.iml
└── src
    ├── main
    │   ├── java
    │   │   └── time
    │   │       └── geekbang
    │   │           └── org
    │   │               └── rw
    │   │                   └── separation
    │   │                       └── v1
    │   │                           ├── RwSeparationV1Application.java
    │   │                           ├── annotation
    │   │                           │   └── ReadOnly.java
    │   │                           ├── aspect
    │   │                           │   └── ReadOnlyAspect.java
    │   │                           ├── datasource
    │   │                           │   ├── DataSourceConfig.java
    │   │                           │   └── DataSourceSelector.java
    │   │                           └── service
    │   │                               ├── OrderService.java
    │   │                               └── impl
    │   │                                   └── OrderServiceImpl.java
    │   └── resources
    │       └── application.properties
    └── test
        └── java
            └── time
                └── geekbang
                    └── org
                        └── rw
                            └── separation
                                └── v1
                                    ├── datasource
                                    │   └── DataSourceTest.java
                                    └── service
                                        └── OrderServiceTest.java
```

## 1.1 application.properties

```properties
# master
master.datasource.driver_class_name=com.mysql.cj.jdbc.Driver
master.datasource.url=jdbc:mysql://localhost:13306/e_commerce?useSSL=false
master.datasource.username=root
master.datasource.password=root
# slave1
slave1.datasource.driver_class_name=${master.datasource.driver_class_name}
slave1.datasource.url=jdbc:mysql://localhost:23306/e_commerce?useSSL=false
slave1.datasource.username=${master.datasource.username}
slave1.datasource.password=${master.datasource.password}
# slave2
slave2.datasource.driver_class_name=${master.datasource.driver_class_name}
slave2.datasource.url=jdbc:mysql://localhost:33306/e_commerce?useSSL=false
slave2.datasource.username=${master.datasource.username}
slave2.datasource.password=${master.datasource.password}
```

## 1.2 DataSourceConfig & DataSourceSelector

```java
@Configuration
public class DataSourceConfig {

    private static final String DEFAULT_DRIVER_CLASS_NAME = com.mysql.cj.jdbc.Driver.class.getSimpleName();

    private final Environment env;

    public DataSourceConfig(Environment env) {
        this.env = env;
    }

    @Primary
    @Bean
    public DataSource master() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("master.datasource.driver_class_name", DEFAULT_DRIVER_CLASS_NAME));
        dataSource.setUrl(env.getProperty("master.datasource.url"));
        dataSource.setUsername(env.getProperty("master.datasource.username"));
        dataSource.setPassword(env.getProperty("master.datasource.password"));
        return dataSource;

    }

    @Bean
    public DataSource slave1() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("slave1.datasource.driver_class_name", DEFAULT_DRIVER_CLASS_NAME));
        dataSource.setUrl(env.getProperty("slave1.datasource.url"));
        dataSource.setUsername(env.getProperty("slave1.datasource.username"));
        dataSource.setPassword(env.getProperty("slave1.datasource.password"));
        return dataSource;
    }

    @Bean
    public DataSource slave2() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("slave2.datasource.driver_class_name", DEFAULT_DRIVER_CLASS_NAME));
        dataSource.setUrl(env.getProperty("slave2.datasource.url"));
        dataSource.setUsername(env.getProperty("slave2.datasource.username"));
        dataSource.setPassword(env.getProperty("slave2.datasource.password"));
        return dataSource;
    }
}
```

```java
@Component
public class DataSourceSelector {

    @Getter
    private final DataSource master;
    private final DataSource slave1;
    private final DataSource slave2;

    public DataSourceSelector(@Qualifier("master") DataSource master,
                              @Qualifier("slave1") DataSource slave1,
                              @Qualifier("slave2") DataSource slave2) {
        this.master = master;
        this.slave1 = slave1;
        this.slave2 = slave2;
    }

    public DataSource selectSlave() {
        return System.currentTimeMillis() % 2 == 0 ? slave1 : slave2;
    }
}
```

## 1.3 ReadOnly & ReadOnlyAspect

```java
@Component
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReadOnly {
}
```

```java
@Slf4j
@Aspect
@Component
public class ReadOnlyAspect {

    private final DataSourceSelector dataSourceSelector;

    public ReadOnlyAspect(DataSourceSelector dataSourceSelector) {
        this.dataSourceSelector = dataSourceSelector;
    }

    @Pointcut("@annotation(time.geekbang.org.rw.separation.v1.annotation.ReadOnly)")
    public void readOnly() {
    }

    @Around("readOnly()")
    public List<Map<String, Object>> changeDataSource(ProceedingJoinPoint point) throws Throwable {
        Object[] argv = point.getArgs();
        Object oldDataSource = argv[0];
        DataSource newDataSource = dataSourceSelector.selectSlave();
        argv[0] = newDataSource;
        if (oldDataSource instanceof DataSource) {
            String oldUrl = ((DataSource) oldDataSource).getConnection().getMetaData().getURL();
            String newUrl = newDataSource.getConnection().getMetaData().getURL();
            log.info("change datasource : {} -> {}", oldUrl, newUrl);
        } else {
            log.info("change datasource : {} -> {}", oldDataSource, newDataSource);
        }
        return (List<Map<String, Object>>) point.proceed(argv);
    }
}
```

## 1.4 OrderService & OrderServiceImpl

```java
public interface OrderService {

    void insert(DataSource dataSource, String sql);

    List<Map<String, Object>> query(DataSource dataSource, String sql);
}
```

```java
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Override
    public void insert(DataSource dataSource, String sql) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            log.info("try to insert, dataSource={}, sql={}", connection.getMetaData().getURL(), sql);
            statement.execute(sql);
        } catch (SQLException e) {
            log.error("insert fail", e);
        }
    }

    @ReadOnly
    @Override
    public List<Map<String, Object>> query(DataSource dataSource, String sql) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            List<Map<String, Object>> entities = new ArrayList<>();
            connection = dataSource.getConnection();
            log.info("try to query, dataSource={}, sql={}", connection.getMetaData().getURL(), sql);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Map<String, Object> data = Maps.newHashMap();
                data.put("id", resultSet.getLong("id"));
                entities.add(data);
            }
            return entities;
        } catch (SQLException e) {
            log.error("query fail", e);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException exception) {
                log.error("close fail", exception);
            }
        }
        return Lists.newArrayList();
    }
}
```

## 1.5 Test

```java
@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class DataSourceTest {

    @Autowired
    private DataSource master;
    @Autowired
    @Qualifier("slave1")
    private DataSource slave1;
    @Autowired
    @Qualifier("slave2")
    private DataSource slave2;

    @Test
    public void databaseSourceTest() throws SQLException {
        assertNotNull(master.getConnection());
        assertNotNull(slave1.getConnection());
        assertNotNull(slave2.getConnection());

        String masterUrl = master.getConnection().getMetaData().getURL();
        String slave1Url = slave1.getConnection().getMetaData().getURL();
        String slave2Url = slave2.getConnection().getMetaData().getURL();

        assertNotEquals(masterUrl, slave1Url);
        assertNotEquals(slave1Url, slave2Url);
        assertNotEquals(slave2Url, masterUrl);

        log.info("masterUrl ==> {}", masterUrl);
        log.info("slave1Url ==> {}", slave1Url);
        log.info("slave2Url ==> {}", slave2Url);

        // output
        // 2020-12-03 02:09:10.508  INFO 19269 --- [           main] t.g.o.r.s.v1.datasource.DataSourceTest   : masterUrl ==> jdbc:mysql://localhost:13306/e_commerce?useSSL=false
        // 2020-12-03 02:09:10.509  INFO 19269 --- [           main] t.g.o.r.s.v1.datasource.DataSourceTest   : slave1Url ==> jdbc:mysql://localhost:23306/e_commerce?useSSL=false
        // 2020-12-03 02:09:10.509  INFO 19269 --- [           main] t.g.o.r.s.v1.datasource.DataSourceTest   : slave2Url ==> jdbc:mysql://localhost:33306/e_commerce?useSSL=false
    }
}
```

```java
@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class OrderServiceTest {

    @Autowired
    private DataSourceSelector dataSourceSelector;
    @Autowired
    private OrderService orderService;

    @Test
    public void insertAndQueryTest() {
        orderService.insert(dataSourceSelector.getMaster(), "insert into `order` VALUES (null,0,0,0,0,0,0,0,0,0)");

        for (int i = 0; i < 2; i++) {
            orderService.query(dataSourceSelector.getMaster(), "select * from `order` limit 3");
        }

        // output
        // 2020-12-03 02:11:39.642  INFO 19310 --- [           main] t.g.o.r.s.v.s.impl.OrderServiceImpl      : try to insert, dataSource=jdbc:mysql://localhost:13306/e_commerce?useSSL=false, sql=insert into `order` VALUES (null,0,0,0,0,0,0,0,0,0)
        // 2020-12-03 02:11:39.691  INFO 19310 --- [           main] t.g.o.r.s.v1.aspect.ReadOnlyAspect       : change datasource : jdbc:mysql://localhost:13306/e_commerce?useSSL=false -> jdbc:mysql://localhost:33306/e_commerce?useSSL=false
        // 2020-12-03 02:11:39.705  INFO 19310 --- [           main] t.g.o.r.s.v.s.impl.OrderServiceImpl      : try to query, dataSource=jdbc:mysql://localhost:33306/e_commerce?useSSL=false, sql=select * from `order` limit 3
        // 2020-12-03 02:11:39.747  INFO 19310 --- [           main] t.g.o.r.s.v1.aspect.ReadOnlyAspect       : change datasource : jdbc:mysql://localhost:13306/e_commerce?useSSL=false -> jdbc:mysql://localhost:23306/e_commerce?useSSL=false
        // 2020-12-03 02:11:39.763  INFO 19310 --- [           main] t.g.o.r.s.v.s.impl.OrderServiceImpl      : try to query, dataSource=jdbc:mysql://localhost:23306/e_commerce?useSSL=false, sql=select * from `order` limit 3
    }
}
```