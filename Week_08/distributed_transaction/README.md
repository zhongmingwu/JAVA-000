# 1 MySQL

## 1.1 docker-compose.yml

```yaml
version: '3.8'

services:

  mysql_1:
    image: mysql:latest
    command: --default-authentication-plugin=mysql_native_password
    restart: always
    hostname: mysql_1
    volumes:
      - ./sql:/docker-entrypoint-initdb.d
    environment:
      MYSQL_ROOT_PASSWORD: 123456
      MYSQL_DATABASE: e_commerce
    ports:
      - "13306:3306"

  mysql_2:
    image: mysql:latest
    command: --default-authentication-plugin=mysql_native_password
    restart: always
    hostname: mysql_2
    volumes:
      - ./sql:/docker-entrypoint-initdb.d
    environment:
      MYSQL_ROOT_PASSWORD: 123456
      MYSQL_DATABASE: e_commerce
    ports:
      - "23306:3306"
```

## 1.2 init.sql

```sql
USE e_commerce;
CREATE TABLE IF NOT EXISTS t_order_0 (id BIGINT NOT NULL PRIMARY KEY, user_id INT NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE t_order_1 LIKE t_order_0;
```

## 1.3 up

```bash
$ docker-compose -f mysql/docker-compose.yml up -d
Creating network "mysql_default" with the default driver
Creating mysql_mysql_1_1 ... done
Creating mysql_mysql_2_1 ... done

$ docker-compose -f mysql/docker-compose.yml ps -a
     Name                    Command               State                 Ports
---------------------------------------------------------------------------------------------
mysql_mysql_1_1   docker-entrypoint.sh --def ...   Up      0.0.0.0:13306->3306/tcp, 33060/tcp
mysql_mysql_2_1   docker-entrypoint.sh --def ...   Up      0.0.0.0:23306->3306/tcp, 33060/tcp
```

# 2 Xa Transaction

source code : [xa-transaction](https://github.com/zhongmingwu/java-training-camp/tree/main/week08/xa-transaction)

```bash
$ tree
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   └── resources
│   │       └── data_source.yaml
│   └── test
│       └── java
│           └── time
│               └── geekbang
│                   └── org
│                       └── XaTransactionTest.java
```

## 2.1 data_source.yaml

```yaml
dataSources:
  ds_0: !!com.zaxxer.hikari.HikariDataSource
    driverClassName: com.mysql.cj.jdbc.Driver
    jdbcUrl: jdbc:mysql://127.0.0.1:13306/e_commerce?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true
    username: root
    password: 123456
    autoCommit: false
  ds_1: !!com.zaxxer.hikari.HikariDataSource
    driverClassName: com.mysql.cj.jdbc.Driver
    jdbcUrl: jdbc:mysql://127.0.0.1:23306/e_commerce?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true
    username: root
    password: 123456
    autoCommit: false

rules:
  - !SHARDING
    tables:
      t_order:
        actualDataNodes: ds_${0..1}.t_order_${0..1}
        databaseStrategy:
          standard:
            shardingColumn: user_id
            shardingAlgorithmName: database_inline
        tableStrategy:
          standard:
            shardingColumn: id
            shardingAlgorithmName: t_order_inline
    bindingTables:
      - t_order

    shardingAlgorithms:
      database_inline:
        type: INLINE
        props:
          algorithm-expression: ds_${user_id % 2}
      t_order_inline:
        type: INLINE
        props:
          algorithm-expression: t_order_${id % 2}

props:
  sql-show: true
```

## 2.2 XaTransactionTest

```java
package time.geekbang.org;

import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.apache.shardingsphere.transaction.core.TransactionTypeHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.*;

import static org.junit.Assert.assertEquals;

public class XaTransactionTest {

    private static final String SQL_TRUNCATE = "TRUNCATE t_order";
    private static final String SQL_INSERT = "INSERT INTO t_order (id, user_id) VALUES (?, ?)";
    private static final String SQL_COUNT = "SELECT COUNT(1) FROM t_order";
    private static final int N = 100;

    private final DataSource dataSource = YamlShardingSphereDataSourceFactory.createDataSource(new File(System.getProperty("dataSourceFile")));
    private Connection connection;

    public XaTransactionTest() throws IOException, SQLException {
    }

    @Before
    public void setUp() throws SQLException {
        connection = dataSource.getConnection();
        clearDate();
        TransactionTypeHolder.set(TransactionType.XA);
    }

    @After
    public void destroy() throws SQLException {
        connection.close();
        clearDate();
        TransactionTypeHolder.clear();
    }

    @Test
    public void xaTransactionTest() throws SQLException {
        for (int i = 0; i < 2; i++) {
            insert(N * (i + 1));
            assertEquals(N, count());
        }

        // output:
        //  insert success, n=100
        //  insert fail, try to rollback, n=200, cause=Duplicate entry '0' for key 't_order_0.PRIMARY'
    }

    private synchronized void clearDate() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(SQL_TRUNCATE);
        connection.commit();
    }

    private synchronized void insert(int n) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT)) {
            connection.setAutoCommit(false);
            for (int i = 0; i < n; i++) {
                preparedStatement.setLong(1, i);
                preparedStatement.setLong(2, i);
                preparedStatement.executeUpdate();
            }
            connection.commit();
            connection.setAutoCommit(true);
            System.out.println("insert success, n=" + n);
        } catch (SQLException e) {
            System.err.println("insert fail, try to rollback, n=" + n + ", cause=" + e.getMessage());
            connection.rollback();
        }
    }

    private synchronized int count() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(SQL_COUNT);
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        throw new RuntimeException("count fail");
    }
}
```

# 3 MySQL Down

```bash
$ docker-compose -f mysql/docker-compose.yml down
Stopping mysql_mysql_1_1 ... done
Stopping mysql_mysql_2_1 ... done
Removing mysql_mysql_1_1 ... done
Removing mysql_mysql_2_1 ... done
Removing network mysql_default
```