# 0 Source Code

[rw-separation-v2](https://github.com/zhongmingwu/java-training-camp/tree/main/week07/rw-separation-v2)

# 1 Files

```bash
$ tree
.
├── HELP.md
├── mvnw
├── mvnw.cmd
├── pom.xml
├── rw-separation-v2.iml
└── src
    ├── main
    │   ├── java
    │   │   └── time
    │   │       └── geekbang
    │   │           └── org
    │   │               └── rw
    │   │                   └── separation
    │   │                       └── v2
    │   │                           └── RwSeparationV2Application.java
    │   └── resources
    │       └── application.yaml
    └── test
        └── java
            └── time
                └── geekbang
                    └── org
                        └── rw
                            └── separation
                                └── v2
                                    └── ShardingSphereTest.java
```

# 1.1 application.yaml

```yml
spring:
  shardingsphere:
    datasource:
      names: master,slave0,slave1
      master:
        type: com.alibaba.druid.pool.DruidDataSource
        driver-class-name: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://localhost:13306/e_commerce?useSSL=false
        username: root
        password: root
      slave0:
        type: ${spring.shardingsphere.datasource.master.type}
        driver-class-name: ${spring.shardingsphere.datasource.master.driver-class-name}
        url: jdbc:mysql://localhost:23306/e_commerce?useSSL=false
        username: ${spring.shardingsphere.datasource.master.username}
        password: ${spring.shardingsphere.datasource.master.password}
      slave1:
        type: ${spring.shardingsphere.datasource.master.type}
        driver-class-name: ${spring.shardingsphere.datasource.master.driver-class-name}
        url: jdbc:mysql://localhost:33306/e_commerce?useSSL=false
        username: ${spring.shardingsphere.datasource.master.username}
        password: ${spring.shardingsphere.datasource.master.password}
    masterslave:
      name: ms
      master-data-source-name: master
      slave-data-source-names: slave0,slave1
    props:
      sql:
        show: true
```

## 1.2 Test

```java
@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class ShardingSphereTest {

    @Resource
    private DataSource dataSource;

    @Test
    public void test() throws SQLException {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();

            String readSql = "select * from `order` limit 3";
            String writeSql = "insert into `order` VALUES (null,0,0,0,0,0,0,0,0,0)";

            for (int i = 0; i < 3; i++) {
                statement.executeQuery(readSql);
            }

            log.info("============= executeUpdate begin =============");
            statement.executeUpdate(writeSql);
            log.info("============= executeUpdate end =============");

            for (int i = 0; i < 3; i++) {
                statement.executeQuery(readSql);
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }

        // output
        // 2020-12-03 03:08:35.433  INFO 21167 --- [           main] ShardingSphere-SQL                       : Logic SQL: select * from `order` limit 3
        // 2020-12-03 03:08:35.433  INFO 21167 --- [           main] ShardingSphere-SQL                       : SQLStatement: SelectStatementContext(super=CommonSQLStatementContext(sqlStatement=org.apache.shardingsphere.sql.parser.sql.statement.dml.SelectStatement@4f4c88f9, tablesContext=org.apache.shardingsphere.sql.parser.binder.segment.table.TablesContext@6a756082), tablesContext=org.apache.shardingsphere.sql.parser.binder.segment.table.TablesContext@6a756082, projectionsContext=ProjectionsContext(startIndex=7, stopIndex=7, distinctRow=false, projections=[ShorthandProjection(owner=Optional.empty, actualColumns=[ColumnProjection(owner=null, name=id, alias=Optional.empty), ColumnProjection(owner=null, name=user_id, alias=Optional.empty), ColumnProjection(owner=null, name=product_id, alias=Optional.empty), ColumnProjection(owner=null, name=amount, alias=Optional.empty), ColumnProjection(owner=null, name=total_price, alias=Optional.empty), ColumnProjection(owner=null, name=address_id, alias=Optional.empty), ColumnProjection(owner=null, name=bank_card_id, alias=Optional.empty), ColumnProjection(owner=null, name=status, alias=Optional.empty), ColumnProjection(owner=null, name=creat_time, alias=Optional.empty), ColumnProjection(owner=null, name=modify_time, alias=Optional.empty)])]), groupByContext=org.apache.shardingsphere.sql.parser.binder.segment.select.groupby.GroupByContext@cb39552, orderByContext=org.apache.shardingsphere.sql.parser.binder.segment.select.orderby.OrderByContext@1f3b992, paginationContext=org.apache.shardingsphere.sql.parser.binder.segment.select.pagination.PaginationContext@2a53f215, containsSubquery=false)
        // 2020-12-03 03:08:35.434  INFO 21167 --- [           main] ShardingSphere-SQL                       : Actual SQL: slave0 ::: select * from `order` limit 3
        // 2020-12-03 03:08:35.440  INFO 21167 --- [           main] ShardingSphere-SQL                       : Logic SQL: select * from `order` limit 3
        // 2020-12-03 03:08:35.440  INFO 21167 --- [           main] ShardingSphere-SQL                       : SQLStatement: SelectStatementContext(super=CommonSQLStatementContext(sqlStatement=org.apache.shardingsphere.sql.parser.sql.statement.dml.SelectStatement@76a14c8d, tablesContext=org.apache.shardingsphere.sql.parser.binder.segment.table.TablesContext@17410c07), tablesContext=org.apache.shardingsphere.sql.parser.binder.segment.table.TablesContext@17410c07, projectionsContext=ProjectionsContext(startIndex=7, stopIndex=7, distinctRow=false, projections=[ShorthandProjection(owner=Optional.empty, actualColumns=[ColumnProjection(owner=null, name=id, alias=Optional.empty), ColumnProjection(owner=null, name=user_id, alias=Optional.empty), ColumnProjection(owner=null, name=product_id, alias=Optional.empty), ColumnProjection(owner=null, name=amount, alias=Optional.empty), ColumnProjection(owner=null, name=total_price, alias=Optional.empty), ColumnProjection(owner=null, name=address_id, alias=Optional.empty), ColumnProjection(owner=null, name=bank_card_id, alias=Optional.empty), ColumnProjection(owner=null, name=status, alias=Optional.empty), ColumnProjection(owner=null, name=creat_time, alias=Optional.empty), ColumnProjection(owner=null, name=modify_time, alias=Optional.empty)])]), groupByContext=org.apache.shardingsphere.sql.parser.binder.segment.select.groupby.GroupByContext@6ee99964, orderByContext=org.apache.shardingsphere.sql.parser.binder.segment.select.orderby.OrderByContext@257ef9ed, paginationContext=org.apache.shardingsphere.sql.parser.binder.segment.select.pagination.PaginationContext@59c70ceb, containsSubquery=false)
        // 2020-12-03 03:08:35.440  INFO 21167 --- [           main] ShardingSphere-SQL                       : Actual SQL: slave1 ::: select * from `order` limit 3
        // 2020-12-03 03:08:35.443  INFO 21167 --- [           main] ShardingSphere-SQL                       : Logic SQL: select * from `order` limit 3
        // 2020-12-03 03:08:35.443  INFO 21167 --- [           main] ShardingSphere-SQL                       : SQLStatement: SelectStatementContext(super=CommonSQLStatementContext(sqlStatement=org.apache.shardingsphere.sql.parser.sql.statement.dml.SelectStatement@361abd01, tablesContext=org.apache.shardingsphere.sql.parser.binder.segment.table.TablesContext@575b5f7d), tablesContext=org.apache.shardingsphere.sql.parser.binder.segment.table.TablesContext@575b5f7d, projectionsContext=ProjectionsContext(startIndex=7, stopIndex=7, distinctRow=false, projections=[ShorthandProjection(owner=Optional.empty, actualColumns=[ColumnProjection(owner=null, name=id, alias=Optional.empty), ColumnProjection(owner=null, name=user_id, alias=Optional.empty), ColumnProjection(owner=null, name=product_id, alias=Optional.empty), ColumnProjection(owner=null, name=amount, alias=Optional.empty), ColumnProjection(owner=null, name=total_price, alias=Optional.empty), ColumnProjection(owner=null, name=address_id, alias=Optional.empty), ColumnProjection(owner=null, name=bank_card_id, alias=Optional.empty), ColumnProjection(owner=null, name=status, alias=Optional.empty), ColumnProjection(owner=null, name=creat_time, alias=Optional.empty), ColumnProjection(owner=null, name=modify_time, alias=Optional.empty)])]), groupByContext=org.apache.shardingsphere.sql.parser.binder.segment.select.groupby.GroupByContext@59bbb974, orderByContext=org.apache.shardingsphere.sql.parser.binder.segment.select.orderby.OrderByContext@7165d530, paginationContext=org.apache.shardingsphere.sql.parser.binder.segment.select.pagination.PaginationContext@12f49ca8, containsSubquery=false)
        // 2020-12-03 03:08:35.443  INFO 21167 --- [           main] ShardingSphere-SQL                       : Actual SQL: slave0 ::: select * from `order` limit 3
        // 2020-12-03 03:08:35.446  INFO 21167 --- [           main] t.g.o.r.s.v2.ShardingSphereTest          : ============= executeUpdate begin =============
        // 2020-12-03 03:08:35.496  INFO 21167 --- [           main] t.g.o.r.s.v2.ShardingSphereTest          : ============= executeUpdate end =============
        // 2020-12-03 03:08:35.497  INFO 21167 --- [           main] ShardingSphere-SQL                       : Logic SQL: select * from `order` limit 3
        // 2020-12-03 03:08:35.497  INFO 21167 --- [           main] ShardingSphere-SQL                       : SQLStatement: SelectStatementContext(super=CommonSQLStatementContext(sqlStatement=org.apache.shardingsphere.sql.parser.sql.statement.dml.SelectStatement@7f9e8421, tablesContext=org.apache.shardingsphere.sql.parser.binder.segment.table.TablesContext@23da79eb), tablesContext=org.apache.shardingsphere.sql.parser.binder.segment.table.TablesContext@23da79eb, projectionsContext=ProjectionsContext(startIndex=7, stopIndex=7, distinctRow=false, projections=[ShorthandProjection(owner=Optional.empty, actualColumns=[ColumnProjection(owner=null, name=id, alias=Optional.empty), ColumnProjection(owner=null, name=user_id, alias=Optional.empty), ColumnProjection(owner=null, name=product_id, alias=Optional.empty), ColumnProjection(owner=null, name=amount, alias=Optional.empty), ColumnProjection(owner=null, name=total_price, alias=Optional.empty), ColumnProjection(owner=null, name=address_id, alias=Optional.empty), ColumnProjection(owner=null, name=bank_card_id, alias=Optional.empty), ColumnProjection(owner=null, name=status, alias=Optional.empty), ColumnProjection(owner=null, name=creat_time, alias=Optional.empty), ColumnProjection(owner=null, name=modify_time, alias=Optional.empty)])]), groupByContext=org.apache.shardingsphere.sql.parser.binder.segment.select.groupby.GroupByContext@168b4cb0, orderByContext=org.apache.shardingsphere.sql.parser.binder.segment.select.orderby.OrderByContext@3e05586b, paginationContext=org.apache.shardingsphere.sql.parser.binder.segment.select.pagination.PaginationContext@35b17c06, containsSubquery=false)
        // 2020-12-03 03:08:35.497  INFO 21167 --- [           main] ShardingSphere-SQL                       : Actual SQL: master ::: select * from `order` limit 3
        // 2020-12-03 03:08:35.502  INFO 21167 --- [           main] ShardingSphere-SQL                       : Logic SQL: select * from `order` limit 3
        // 2020-12-03 03:08:35.502  INFO 21167 --- [           main] ShardingSphere-SQL                       : SQLStatement: SelectStatementContext(super=CommonSQLStatementContext(sqlStatement=org.apache.shardingsphere.sql.parser.sql.statement.dml.SelectStatement@9f674ac, tablesContext=org.apache.shardingsphere.sql.parser.binder.segment.table.TablesContext@1da4b3f9), tablesContext=org.apache.shardingsphere.sql.parser.binder.segment.table.TablesContext@1da4b3f9, projectionsContext=ProjectionsContext(startIndex=7, stopIndex=7, distinctRow=false, projections=[ShorthandProjection(owner=Optional.empty, actualColumns=[ColumnProjection(owner=null, name=id, alias=Optional.empty), ColumnProjection(owner=null, name=user_id, alias=Optional.empty), ColumnProjection(owner=null, name=product_id, alias=Optional.empty), ColumnProjection(owner=null, name=amount, alias=Optional.empty), ColumnProjection(owner=null, name=total_price, alias=Optional.empty), ColumnProjection(owner=null, name=address_id, alias=Optional.empty), ColumnProjection(owner=null, name=bank_card_id, alias=Optional.empty), ColumnProjection(owner=null, name=status, alias=Optional.empty), ColumnProjection(owner=null, name=creat_time, alias=Optional.empty), ColumnProjection(owner=null, name=modify_time, alias=Optional.empty)])]), groupByContext=org.apache.shardingsphere.sql.parser.binder.segment.select.groupby.GroupByContext@23cbbd07, orderByContext=org.apache.shardingsphere.sql.parser.binder.segment.select.orderby.OrderByContext@448b808a, paginationContext=org.apache.shardingsphere.sql.parser.binder.segment.select.pagination.PaginationContext@7e62cfa3, containsSubquery=false)
        // 2020-12-03 03:08:35.502  INFO 21167 --- [           main] ShardingSphere-SQL                       : Actual SQL: master ::: select * from `order` limit 3
        // 2020-12-03 03:08:35.506  INFO 21167 --- [           main] ShardingSphere-SQL                       : Logic SQL: select * from `order` limit 3
        // 2020-12-03 03:08:35.506  INFO 21167 --- [           main] ShardingSphere-SQL                       : SQLStatement: SelectStatementContext(super=CommonSQLStatementContext(sqlStatement=org.apache.shardingsphere.sql.parser.sql.statement.dml.SelectStatement@383c3eb3, tablesContext=org.apache.shardingsphere.sql.parser.binder.segment.table.TablesContext@20ab76ee), tablesContext=org.apache.shardingsphere.sql.parser.binder.segment.table.TablesContext@20ab76ee, projectionsContext=ProjectionsContext(startIndex=7, stopIndex=7, distinctRow=false, projections=[ShorthandProjection(owner=Optional.empty, actualColumns=[ColumnProjection(owner=null, name=id, alias=Optional.empty), ColumnProjection(owner=null, name=user_id, alias=Optional.empty), ColumnProjection(owner=null, name=product_id, alias=Optional.empty), ColumnProjection(owner=null, name=amount, alias=Optional.empty), ColumnProjection(owner=null, name=total_price, alias=Optional.empty), ColumnProjection(owner=null, name=address_id, alias=Optional.empty), ColumnProjection(owner=null, name=bank_card_id, alias=Optional.empty), ColumnProjection(owner=null, name=status, alias=Optional.empty), ColumnProjection(owner=null, name=creat_time, alias=Optional.empty), ColumnProjection(owner=null, name=modify_time, alias=Optional.empty)])]), groupByContext=org.apache.shardingsphere.sql.parser.binder.segment.select.groupby.GroupByContext@485caa8f, orderByContext=org.apache.shardingsphere.sql.parser.binder.segment.select.orderby.OrderByContext@2703d91, paginationContext=org.apache.shardingsphere.sql.parser.binder.segment.select.pagination.PaginationContext@5be052ca, containsSubquery=false)
        // 2020-12-03 03:08:35.507  INFO 21167 --- [           main] ShardingSphere-SQL                       : Actual SQL: master ::: select * from `order` limit 3
    }
}
```

