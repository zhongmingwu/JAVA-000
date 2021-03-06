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
    environment:
      MYSQL_ROOT_PASSWORD: 123456
      MYSQL_DATABASE: e_commerce_1
    ports:
      - "13306:3306"

  mysql_2:
    image: mysql:latest
    command: --default-authentication-plugin=mysql_native_password
    restart: always
    hostname: mysql_2
    environment:
      MYSQL_ROOT_PASSWORD: 123456
      MYSQL_DATABASE: e_commerce_2
    ports:
      - "23306:3306"
```

## 1.2 up

```bash
$ docker-compose -f mysql/docker-compose.yml up -d
Creating network "mysql_default" with the default driver
Creating mysql_mysql_2_1 ... done
Creating mysql_mysql_1_1 ... done

$ docker-compose -f mysql/docker-compose.yml ps -a
     Name                    Command               State                 Ports
---------------------------------------------------------------------------------------------
mysql_mysql_1_1   docker-entrypoint.sh --def ...   Up      0.0.0.0:13306->3306/tcp, 33060/tcp
mysql_mysql_2_1   docker-entrypoint.sh --def ...   Up      0.0.0.0:23306->3306/tcp, 33060/tcp
```

# 2 Sharding Proxy

## 2.1 server.yaml

```yaml
authentication:
  users:
    root:
      password: 123456

props:
  max-connections-size-per-query: 1
  acceptor-size: 16
  executor-size: 16
  proxy-frontend-flush-threshold: 128
  proxy-transaction-type: LOCAL
  proxy-opentracing-enabled: false
  proxy-hint-enabled: false
  query-with-cipher-column: true
  sql-show: true
  check-table-metadata-enabled: false
```

## 2.2 config-e_commerce.yaml

```yaml
schemaName: e_commerce

dataSourceCommon:
  username: root
  password: 123456
  connectionTimeoutMilliseconds: 30000
  idleTimeoutMilliseconds: 60000
  maxLifetimeMilliseconds: 1800000
  maxPoolSize: 50
  minPoolSize: 1
  maintenanceIntervalMilliseconds: 30000

dataSources:
  ds_0:
    url: jdbc:mysql://127.0.0.1:13306/e_commerce_1?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true
  ds_1:
    url: jdbc:mysql://127.0.0.1:23306/e_commerce_2?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true

rules:
  - !SHARDING
    tables:
      order:
        actualDataNodes: ds_${0..1}.order_${0..15}
        tableStrategy:
          standard:
            shardingColumn: id
            shardingAlgorithmName: order_inline
    defaultDatabaseStrategy:
      standard:
        shardingColumn: user_id
        shardingAlgorithmName: database_inline
    shardingAlgorithms:
      database_inline:
        type: INLINE
        props:
          algorithm-expression: ds_${user_id % 2}
      order_inline:
        type: INLINE
        props:
          algorithm-expression: order_${id % 16}
```

## 2.3 start

```bash
$ sharding_proxy/bin/start.sh 33306
Starting the ShardingSphere-Proxy ...
The port is 33306
sharding_proxy/bin/start.sh: line 57: fg: no job control
...
```

# 3 Database Operation

## 3.1 Create Table

```sql
mysql> CREATE TABLE IF NOT EXISTS `order` (
    ->     `id` BIGINT(20) NOT NULL PRIMARY KEY,
    ->     `user_id` BIGINT(20) NOT NULL
    -> ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
Query OK, 0 rows affected (0.54 sec)
```

```sql
[INFO ] 17:01:45.792 [ShardingSphere-Command-4] ShardingSphere-SQL - Logic SQL: CREATE TABLE IF NOT EXISTS `order` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
[INFO ] 17:01:45.792 [ShardingSphere-Command-4] ShardingSphere-SQL - SQLStatement: MySQLCreateTableStatement(isNotExisted=true)
[INFO ] 17:01:45.792 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_0 ::: CREATE TABLE IF NOT EXISTS `order_0` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
[INFO ] 17:01:45.792 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_0 ::: CREATE TABLE IF NOT EXISTS `order_1` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
[INFO ] 17:01:45.792 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_0 ::: CREATE TABLE IF NOT EXISTS `order_2` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
[INFO ] 17:01:45.792 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_0 ::: CREATE TABLE IF NOT EXISTS `order_3` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
[INFO ] 17:01:45.792 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_0 ::: CREATE TABLE IF NOT EXISTS `order_4` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
[INFO ] 17:01:45.792 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_0 ::: CREATE TABLE IF NOT EXISTS `order_5` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
[INFO ] 17:01:45.792 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_0 ::: CREATE TABLE IF NOT EXISTS `order_6` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
[INFO ] 17:01:45.792 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_0 ::: CREATE TABLE IF NOT EXISTS `order_7` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
[INFO ] 17:01:45.792 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_0 ::: CREATE TABLE IF NOT EXISTS `order_8` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
[INFO ] 17:01:45.792 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_0 ::: CREATE TABLE IF NOT EXISTS `order_9` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
[INFO ] 17:01:45.792 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_0 ::: CREATE TABLE IF NOT EXISTS `order_10` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
[INFO ] 17:01:45.792 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_0 ::: CREATE TABLE IF NOT EXISTS `order_11` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
[INFO ] 17:01:45.793 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_0 ::: CREATE TABLE IF NOT EXISTS `order_12` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
[INFO ] 17:01:45.793 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_0 ::: CREATE TABLE IF NOT EXISTS `order_13` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
[INFO ] 17:01:45.793 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_0 ::: CREATE TABLE IF NOT EXISTS `order_14` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
[INFO ] 17:01:45.793 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_0 ::: CREATE TABLE IF NOT EXISTS `order_15` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
[INFO ] 17:01:45.793 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_1 ::: CREATE TABLE IF NOT EXISTS `order_0` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
[INFO ] 17:01:45.793 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_1 ::: CREATE TABLE IF NOT EXISTS `order_1` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
[INFO ] 17:01:45.793 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_1 ::: CREATE TABLE IF NOT EXISTS `order_2` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
[INFO ] 17:01:45.793 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_1 ::: CREATE TABLE IF NOT EXISTS `order_3` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
[INFO ] 17:01:45.793 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_1 ::: CREATE TABLE IF NOT EXISTS `order_4` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
[INFO ] 17:01:45.793 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_1 ::: CREATE TABLE IF NOT EXISTS `order_5` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
[INFO ] 17:01:45.793 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_1 ::: CREATE TABLE IF NOT EXISTS `order_6` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
[INFO ] 17:01:45.793 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_1 ::: CREATE TABLE IF NOT EXISTS `order_7` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
[INFO ] 17:01:45.794 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_1 ::: CREATE TABLE IF NOT EXISTS `order_8` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
[INFO ] 17:01:45.794 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_1 ::: CREATE TABLE IF NOT EXISTS `order_9` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
[INFO ] 17:01:45.794 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_1 ::: CREATE TABLE IF NOT EXISTS `order_10` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
[INFO ] 17:01:45.794 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_1 ::: CREATE TABLE IF NOT EXISTS `order_11` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
[INFO ] 17:01:45.794 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_1 ::: CREATE TABLE IF NOT EXISTS `order_12` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
[INFO ] 17:01:45.794 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_1 ::: CREATE TABLE IF NOT EXISTS `order_13` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
[INFO ] 17:01:45.794 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_1 ::: CREATE TABLE IF NOT EXISTS `order_14` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
[INFO ] 17:01:45.794 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_1 ::: CREATE TABLE IF NOT EXISTS `order_15` (
    `id` BIGINT(20) NOT NULL PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
```

## 3.2 Insert

```sql
mysql> insert into `order` values (5,10);
Query OK, 1 row affected (0.16 sec)
```

```sql
[INFO ] 17:06:45.888 [ShardingSphere-Command-5] ShardingSphere-SQL - Logic SQL: insert into `order` values (5,10)
[INFO ] 17:06:45.888 [ShardingSphere-Command-5] ShardingSphere-SQL - SQLStatement: MySQLInsertStatement(setAssignment=Optional.empty, onDuplicateKeyColumns=Optional.empty)
[INFO ] 17:06:45.888 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_0 ::: insert into `order_5` values (5, 10)
```

```sql
mysql> insert into `order` values (7,21);
Query OK, 1 row affected (0.04 sec)
```

```sql
[INFO ] 17:07:48.922 [ShardingSphere-Command-6] ShardingSphere-SQL - Logic SQL: insert into `order` values (7,21)
[INFO ] 17:07:48.922 [ShardingSphere-Command-6] ShardingSphere-SQL - SQLStatement: MySQLInsertStatement(setAssignment=Optional.empty, onDuplicateKeyColumns=Optional.empty)
[INFO ] 17:07:48.923 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_1 ::: insert into `order_7` values (7, 21)
```

## 3.3 Select

```sql
mysql> select * from `order` where user_id = 21;
+----+---------+
| id | user_id |
+----+---------+
|  7 |      21 |
+----+---------+
1 row in set (0.11 sec)
```

```sql
[INFO ] 17:09:11.653 [ShardingSphere-Command-7] ShardingSphere-SQL - Logic SQL: select * from `order` where user_id = 21
[INFO ] 17:09:11.654 [ShardingSphere-Command-7] ShardingSphere-SQL - SQLStatement: MySQLSelectStatement(limit=Optional.empty, lock=Optional.empty)
[INFO ] 17:09:11.654 [ShardingSphere-Command-7] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from `order_0` where user_id = 21
[INFO ] 17:09:11.654 [ShardingSphere-Command-7] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from `order_1` where user_id = 21
[INFO ] 17:09:11.654 [ShardingSphere-Command-7] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from `order_2` where user_id = 21
[INFO ] 17:09:11.654 [ShardingSphere-Command-7] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from `order_3` where user_id = 21
[INFO ] 17:09:11.654 [ShardingSphere-Command-7] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from `order_4` where user_id = 21
[INFO ] 17:09:11.654 [ShardingSphere-Command-7] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from `order_5` where user_id = 21
[INFO ] 17:09:11.654 [ShardingSphere-Command-7] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from `order_6` where user_id = 21
[INFO ] 17:09:11.655 [ShardingSphere-Command-7] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from `order_7` where user_id = 21
[INFO ] 17:09:11.655 [ShardingSphere-Command-7] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from `order_8` where user_id = 21
[INFO ] 17:09:11.655 [ShardingSphere-Command-7] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from `order_9` where user_id = 21
[INFO ] 17:09:11.655 [ShardingSphere-Command-7] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from `order_10` where user_id = 21
[INFO ] 17:09:11.655 [ShardingSphere-Command-7] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from `order_11` where user_id = 21
[INFO ] 17:09:11.655 [ShardingSphere-Command-7] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from `order_12` where user_id = 21
[INFO ] 17:09:11.655 [ShardingSphere-Command-7] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from `order_13` where user_id = 21
[INFO ] 17:09:11.655 [ShardingSphere-Command-7] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from `order_14` where user_id = 21
[INFO ] 17:09:11.655 [ShardingSphere-Command-7] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from `order_15` where user_id = 21
```

```sql
mysql> select * from `order` where id = 7 and user_id = 21;
+----+---------+
| id | user_id |
+----+---------+
|  7 |      21 |
+----+---------+
1 row in set (0.08 sec)
```

```sql
[INFO ] 17:10:25.779 [ShardingSphere-Command-8] ShardingSphere-SQL - Logic SQL: select * from `order` where id = 7 and user_id = 21
[INFO ] 17:10:25.780 [ShardingSphere-Command-8] ShardingSphere-SQL - SQLStatement: MySQLSelectStatement(limit=Optional.empty, lock=Optional.empty)
[INFO ] 17:10:25.780 [ShardingSphere-Command-8] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from `order_7` where id = 7 and user_id = 21
```

## 3.4 Truncate

```sql
mysql> truncate `order`;
Query OK, 0 rows affected (0.41 sec)
```

```sql
[INFO ] 17:11:59.100 [ShardingSphere-Command-9] ShardingSphere-SQL - Logic SQL: truncate `order`
[INFO ] 17:11:59.100 [ShardingSphere-Command-9] ShardingSphere-SQL - SQLStatement: MySQLTruncateStatement()
[INFO ] 17:11:59.100 [ShardingSphere-Command-9] ShardingSphere-SQL - Actual SQL: ds_0 ::: truncate `order_0`
[INFO ] 17:11:59.101 [ShardingSphere-Command-9] ShardingSphere-SQL - Actual SQL: ds_0 ::: truncate `order_1`
[INFO ] 17:11:59.101 [ShardingSphere-Command-9] ShardingSphere-SQL - Actual SQL: ds_0 ::: truncate `order_2`
[INFO ] 17:11:59.101 [ShardingSphere-Command-9] ShardingSphere-SQL - Actual SQL: ds_0 ::: truncate `order_3`
[INFO ] 17:11:59.101 [ShardingSphere-Command-9] ShardingSphere-SQL - Actual SQL: ds_0 ::: truncate `order_4`
[INFO ] 17:11:59.101 [ShardingSphere-Command-9] ShardingSphere-SQL - Actual SQL: ds_0 ::: truncate `order_5`
[INFO ] 17:11:59.101 [ShardingSphere-Command-9] ShardingSphere-SQL - Actual SQL: ds_0 ::: truncate `order_6`
[INFO ] 17:11:59.101 [ShardingSphere-Command-9] ShardingSphere-SQL - Actual SQL: ds_0 ::: truncate `order_7`
[INFO ] 17:11:59.101 [ShardingSphere-Command-9] ShardingSphere-SQL - Actual SQL: ds_0 ::: truncate `order_8`
[INFO ] 17:11:59.101 [ShardingSphere-Command-9] ShardingSphere-SQL - Actual SQL: ds_0 ::: truncate `order_9`
[INFO ] 17:11:59.101 [ShardingSphere-Command-9] ShardingSphere-SQL - Actual SQL: ds_0 ::: truncate `order_10`
[INFO ] 17:11:59.101 [ShardingSphere-Command-9] ShardingSphere-SQL - Actual SQL: ds_0 ::: truncate `order_11`
[INFO ] 17:11:59.102 [ShardingSphere-Command-9] ShardingSphere-SQL - Actual SQL: ds_0 ::: truncate `order_12`
[INFO ] 17:11:59.102 [ShardingSphere-Command-9] ShardingSphere-SQL - Actual SQL: ds_0 ::: truncate `order_13`
[INFO ] 17:11:59.102 [ShardingSphere-Command-9] ShardingSphere-SQL - Actual SQL: ds_0 ::: truncate `order_14`
[INFO ] 17:11:59.102 [ShardingSphere-Command-9] ShardingSphere-SQL - Actual SQL: ds_0 ::: truncate `order_15`
[INFO ] 17:11:59.102 [ShardingSphere-Command-9] ShardingSphere-SQL - Actual SQL: ds_1 ::: truncate `order_0`
[INFO ] 17:11:59.102 [ShardingSphere-Command-9] ShardingSphere-SQL - Actual SQL: ds_1 ::: truncate `order_1`
[INFO ] 17:11:59.102 [ShardingSphere-Command-9] ShardingSphere-SQL - Actual SQL: ds_1 ::: truncate `order_2`
[INFO ] 17:11:59.102 [ShardingSphere-Command-9] ShardingSphere-SQL - Actual SQL: ds_1 ::: truncate `order_3`
[INFO ] 17:11:59.103 [ShardingSphere-Command-9] ShardingSphere-SQL - Actual SQL: ds_1 ::: truncate `order_4`
[INFO ] 17:11:59.103 [ShardingSphere-Command-9] ShardingSphere-SQL - Actual SQL: ds_1 ::: truncate `order_5`
[INFO ] 17:11:59.103 [ShardingSphere-Command-9] ShardingSphere-SQL - Actual SQL: ds_1 ::: truncate `order_6`
[INFO ] 17:11:59.103 [ShardingSphere-Command-9] ShardingSphere-SQL - Actual SQL: ds_1 ::: truncate `order_7`
[INFO ] 17:11:59.103 [ShardingSphere-Command-9] ShardingSphere-SQL - Actual SQL: ds_1 ::: truncate `order_8`
[INFO ] 17:11:59.103 [ShardingSphere-Command-9] ShardingSphere-SQL - Actual SQL: ds_1 ::: truncate `order_9`
[INFO ] 17:11:59.103 [ShardingSphere-Command-9] ShardingSphere-SQL - Actual SQL: ds_1 ::: truncate `order_10`
[INFO ] 17:11:59.103 [ShardingSphere-Command-9] ShardingSphere-SQL - Actual SQL: ds_1 ::: truncate `order_11`
[INFO ] 17:11:59.103 [ShardingSphere-Command-9] ShardingSphere-SQL - Actual SQL: ds_1 ::: truncate `order_12`
[INFO ] 17:11:59.104 [ShardingSphere-Command-9] ShardingSphere-SQL - Actual SQL: ds_1 ::: truncate `order_13`
[INFO ] 17:11:59.104 [ShardingSphere-Command-9] ShardingSphere-SQL - Actual SQL: ds_1 ::: truncate `order_14`
[INFO ] 17:11:59.104 [ShardingSphere-Command-9] ShardingSphere-SQL - Actual SQL: ds_1 ::: truncate `order_15`
```

# 4 Stop & Down

```bash
$ sharding_proxy/bin/stop.sh
Stopping the ShardingSphere-Proxy ....OK!
PID: 19382
```

```bash
$ docker-compose -f mysql/docker-compose.yml down
Stopping mysql_mysql_1_1 ... done
Stopping mysql_mysql_2_1 ... done
Removing mysql_mysql_1_1 ... done
Removing mysql_mysql_2_1 ... done
Removing network mysql_default
```