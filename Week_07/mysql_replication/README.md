# 1 Files

```bash
$ tree
.
├── README.md
├── docker-compose.yml
├── master
│   ├── Dockerfile
│   └── my.cnf
├── slave_1
│   ├── Dockerfile
│   └── my.cnf
└── slave_2
    ├── Dockerfile
    └── my.cnf

3 directories, 8 files
```

## 1.1 docker-compose.yml

```yml
version: '2'

services:
  mysql_master:
    build:
      context: .
      dockerfile: master/Dockerfile
    environment:
      - "MYSQL_ROOT_PASSWORD=root"
    ports:
      - "13306:3306"
    restart: always
    hostname: mysql_master
    networks:
      - docker_bridge

  mysql_slave_1:
    build:
      context: .
      dockerfile: slave_1/Dockerfile
    environment:
      - "MYSQL_ROOT_PASSWORD=root"
    ports:
      - "23306:3306"
    restart: always
    hostname: mysql_slave_1
    networks:
      - docker_bridge

  mysql_slave_2:
    build:
      context: .
      dockerfile: slave_2/Dockerfile
    environment:
      - "MYSQL_ROOT_PASSWORD=root"
    ports:
      - "33306:3306"
    restart: always
    hostname: mysql_slave_2
    networks:
      - docker_bridge

networks:
  docker_bridge:
    driver: bridge
```

## 1.2 master

### 1.2.1 Dockerfile

```dockerfile
FROM mysql:5.7
ADD ./master/my.cnf /etc/mysql/my.cnf
```

### 1.2.2 my.cnf

```mysql
[mysqld]
server_id=10
log-bin=master_bin
```

## 1.3 slave_1

### 1.3.1 Dockerfile

```dockerfile
FROM mysql:5.7
ADD ./slave_1/my.cnf /etc/mysql/my.cnf
```

### 1.3.2 my.cnf

```mysql
[mysqld]
server_id=21
log-bin=slave_1_bin
relay_log=slave_1_relay_bin
log_slave_updates=1
read_only=1
```

## 1.4 slave_2

### 1.4.1 Dockerfile

```dockerfile
FROM mysql:5.7
ADD ./slave_2/my.cnf /etc/mysql/my.cnf
```

### 1.4.2 my.cnf

```mysql
[mysqld]
server_id=22
log-bin=slave_2_bin
relay_log=slave_2_relay_bin
log_slave_updates=1
read_only=1
```

# 2 Steps

## 2.1 build & up

```bash
$ docker-compose up --build -d
Creating network "mysql_replication_docker_bridge" with driver "bridge"
Building mysql_master
Step 1/2 : FROM mysql:5.7
 ---> ae0658fdbad5
Step 2/2 : ADD ./master/my.cnf /etc/mysql/my.cnf
 ---> c40edb63fbff

Successfully built c40edb63fbff
Successfully tagged mysql_replication_mysql_master:latest
Building mysql_slave_1
Step 1/2 : FROM mysql:5.7
 ---> ae0658fdbad5
Step 2/2 : ADD ./slave_1/my.cnf /etc/mysql/my.cnf
 ---> c87cb498164a

Successfully built c87cb498164a
Successfully tagged mysql_replication_mysql_slave_1:latest
Building mysql_slave_2
Step 1/2 : FROM mysql:5.7
 ---> ae0658fdbad5
Step 2/2 : ADD ./slave_2/my.cnf /etc/mysql/my.cnf
 ---> fb50987331eb

Successfully built fb50987331eb
Successfully tagged mysql_replication_mysql_slave_2:latest
Creating mysql_replication_mysql_master_1  ... done
Creating mysql_replication_mysql_slave_1_1 ... done
Creating mysql_replication_mysql_slave_2_1 ... done
```

```bash
$ docker-compose ps -a
              Name                            Command             State                 Ports
------------------------------------------------------------------------------------------------------------
mysql_replication_mysql_master_1    docker-entrypoint.sh mysqld   Up      0.0.0.0:13306->3306/tcp, 33060/tcp
mysql_replication_mysql_slave_1_1   docker-entrypoint.sh mysqld   Up      0.0.0.0:23306->3306/tcp, 33060/tcp
mysql_replication_mysql_slave_2_1   docker-entrypoint.sh mysqld   Up      0.0.0.0:33306->3306/tcp, 33060/tcp
```

## 2.2 network

```bash
$ docker network ls
NETWORK ID          NAME                              DRIVER              SCOPE
11fbc12a2bfd        bridge                            bridge              local
07b11c107114        host                              host                local
042b60ebf260        mysql_replication_docker_bridge   bridge              local
b6feea96fd9b        none                              null                local
```

```bash
$ docker inspect mysql_replication_docker_bridge
[
    {
        "Name": "mysql_replication_docker_bridge",
        "Id": "042b60ebf2608f414f740dd72d9affab0d9b5a9eb0849072e2463ab24d99adbc",
        "Created": "2020-12-02T15:30:33.1309832Z",
        "Scope": "local",
        "Driver": "bridge",
        "EnableIPv6": false,
        "IPAM": {
            "Driver": "default",
            "Options": null,
            "Config": [
                {
                    "Subnet": "172.31.0.0/16",
                    "Gateway": "172.31.0.1"
                }
            ]
        },
        "Internal": false,
        "Attachable": true,
        "Ingress": false,
        "ConfigFrom": {
            "Network": ""
        },
        "ConfigOnly": false,
        "Containers": {
            "2d3eb558a9dc7b862f935905bcb63c1dcfe2bdace849eeff168e82452ead2b0a": {
                "Name": "mysql_replication_mysql_slave_2_1",
                "EndpointID": "5695e7f994a68e9c72993e6c5d99c132aa5be5cc4d20856d799a687a205ceb13",
                "MacAddress": "02:42:ac:1f:00:03",
                "IPv4Address": "172.31.0.3/16",
                "IPv6Address": ""
            },
            "c8620044cad25ee10c332562b353a556f5954aa731303f0aaf4f76c34d7c630f": {
                "Name": "mysql_replication_mysql_master_1",
                "EndpointID": "7dba11fe3c982761c74e6290692a746790bf125be5685aaa6c5124510ef43ee0",
                "MacAddress": "02:42:ac:1f:00:02",
                "IPv4Address": "172.31.0.2/16",
                "IPv6Address": ""
            },
            "cca3a489b9e6b914439e6dba31eccaa4d52c11fad9b122248bea209524e4310c": {
                "Name": "mysql_replication_mysql_slave_1_1",
                "EndpointID": "7a93655982d98d3dbaaa430eb5a4b60c77e122de0f8530d15a5c72bd99e06c18",
                "MacAddress": "02:42:ac:1f:00:04",
                "IPv4Address": "172.31.0.4/16",
                "IPv6Address": ""
            }
        },
        "Options": {},
        "Labels": {
            "com.docker.compose.network": "docker_bridge",
            "com.docker.compose.project": "mysql_replication",
            "com.docker.compose.version": "1.27.4"
        }
    }
]
```

## 2.3 master status & slave status

### 2.3.1 master

```mysql
mysql> show master status;
+-------------------+----------+--------------+------------------+-------------------+
| File              | Position | Binlog_Do_DB | Binlog_Ignore_DB | Executed_Gtid_Set |
+-------------------+----------+--------------+------------------+-------------------+
| master_bin.000003 |      154 |              |                  |                   |
+-------------------+----------+--------------+------------------+-------------------+
1 row in set (0.01 sec)

mysql> show slave status;
Empty set (0.00 sec)
```

### 2.3.2 slave_1

```mysql
mysql> show master status;
+--------------------+----------+--------------+------------------+-------------------+
| File               | Position | Binlog_Do_DB | Binlog_Ignore_DB | Executed_Gtid_Set |
+--------------------+----------+--------------+------------------+-------------------+
| slave_1_bin.000003 |      154 |              |                  |                   |
+--------------------+----------+--------------+------------------+-------------------+
1 row in set (0.01 sec)

mysql> show slave status;
Empty set (0.00 sec)
```

### 2.3.3 slave_2

```mysql
mysql> show master status;
+--------------------+----------+--------------+------------------+-------------------+
| File               | Position | Binlog_Do_DB | Binlog_Ignore_DB | Executed_Gtid_Set |
+--------------------+----------+--------------+------------------+-------------------+
| slave_2_bin.000003 |      154 |              |                  |                   |
+--------------------+----------+--------------+------------------+-------------------+
1 row in set (0.01 sec)

mysql> show slave status;
Empty set (0.00 sec)
```

## 2.4 start slave

### 2.4.1 slave_1

```mysql
mysql> change master to master_host='mysql_master',master_user='root',master_password='root',master_log_file='master_bin.000003',master_log_pos=154;
Query OK, 0 rows affected, 1 warning (0.02 sec)

mysql> start slave;
Query OK, 0 rows affected (0.01 sec)
```

```mysql
mysql> show slave status\G;
*************************** 1. row ***************************
               Slave_IO_State: Waiting for master to send event
                  Master_Host: mysql_master
                  Master_User: root
                  Master_Port: 3306
                Connect_Retry: 60
              Master_Log_File: master_bin.000003
          Read_Master_Log_Pos: 154
               Relay_Log_File: slave_1_relay_bin.000002
                Relay_Log_Pos: 321
        Relay_Master_Log_File: master_bin.000003
             Slave_IO_Running: Yes
            Slave_SQL_Running: Yes
              Replicate_Do_DB:
          Replicate_Ignore_DB:
           Replicate_Do_Table:
       Replicate_Ignore_Table:
      Replicate_Wild_Do_Table:
  Replicate_Wild_Ignore_Table:
                   Last_Errno: 0
                   Last_Error:
                 Skip_Counter: 0
          Exec_Master_Log_Pos: 154
              Relay_Log_Space: 530
              Until_Condition: None
               Until_Log_File:
                Until_Log_Pos: 0
           Master_SSL_Allowed: No
           Master_SSL_CA_File:
           Master_SSL_CA_Path:
              Master_SSL_Cert:
            Master_SSL_Cipher:
               Master_SSL_Key:
        Seconds_Behind_Master: 0
Master_SSL_Verify_Server_Cert: No
                Last_IO_Errno: 0
                Last_IO_Error:
               Last_SQL_Errno: 0
               Last_SQL_Error:
  Replicate_Ignore_Server_Ids:
             Master_Server_Id: 10
                  Master_UUID: 5471db9a-34b3-11eb-8fd1-0242ac1f0002
             Master_Info_File: /var/lib/mysql/master.info
                    SQL_Delay: 0
          SQL_Remaining_Delay: NULL
      Slave_SQL_Running_State: Slave has read all relay log; waiting for more updates
           Master_Retry_Count: 86400
                  Master_Bind:
      Last_IO_Error_Timestamp:
     Last_SQL_Error_Timestamp:
               Master_SSL_Crl:
           Master_SSL_Crlpath:
           Retrieved_Gtid_Set:
            Executed_Gtid_Set:
                Auto_Position: 0
         Replicate_Rewrite_DB:
                 Channel_Name:
           Master_TLS_Version:
1 row in set (0.00 sec)
```

### 2.4.2 slave_2

```mysql
mysql> change master to master_host='mysql_master',master_user='root',master_password='root',master_log_file='master_bin.000003',master_log_pos=154;
Query OK, 0 rows affected, 1 warning (0.02 sec)

mysql> start slave;
Query OK, 0 rows affected (0.01 sec)
```

```mysql
mysql> show slave status\G;
*************************** 1. row ***************************
               Slave_IO_State: Waiting for master to send event
                  Master_Host: mysql_master
                  Master_User: root
                  Master_Port: 3306
                Connect_Retry: 60
              Master_Log_File: master_bin.000003
          Read_Master_Log_Pos: 154
               Relay_Log_File: slave_2_relay_bin.000002
                Relay_Log_Pos: 321
        Relay_Master_Log_File: master_bin.000003
             Slave_IO_Running: Yes
            Slave_SQL_Running: Yes
              Replicate_Do_DB:
          Replicate_Ignore_DB:
           Replicate_Do_Table:
       Replicate_Ignore_Table:
      Replicate_Wild_Do_Table:
  Replicate_Wild_Ignore_Table:
                   Last_Errno: 0
                   Last_Error:
                 Skip_Counter: 0
          Exec_Master_Log_Pos: 154
              Relay_Log_Space: 530
              Until_Condition: None
               Until_Log_File:
                Until_Log_Pos: 0
           Master_SSL_Allowed: No
           Master_SSL_CA_File:
           Master_SSL_CA_Path:
              Master_SSL_Cert:
            Master_SSL_Cipher:
               Master_SSL_Key:
        Seconds_Behind_Master: 0
Master_SSL_Verify_Server_Cert: No
                Last_IO_Errno: 0
                Last_IO_Error:
               Last_SQL_Errno: 0
               Last_SQL_Error:
  Replicate_Ignore_Server_Ids:
             Master_Server_Id: 10
                  Master_UUID: 5471db9a-34b3-11eb-8fd1-0242ac1f0002
             Master_Info_File: /var/lib/mysql/master.info
                    SQL_Delay: 0
          SQL_Remaining_Delay: NULL
      Slave_SQL_Running_State: Slave has read all relay log; waiting for more updates
           Master_Retry_Count: 86400
                  Master_Bind:
      Last_IO_Error_Timestamp:
     Last_SQL_Error_Timestamp:
               Master_SSL_Crl:
           Master_SSL_Crlpath:
           Retrieved_Gtid_Set:
            Executed_Gtid_Set:
                Auto_Position: 0
         Replicate_Rewrite_DB:
                 Channel_Name:
           Master_TLS_Version:
1 row in set (0.01 sec)
```

## 2.5 create database

### 2.5.1 mysql_master

```mysql
mysql> create database master_test_db;
Query OK, 1 row affected (0.00 sec)

mysql> show master status;
+-------------------+----------+--------------+------------------+-------------------+
| File              | Position | Binlog_Do_DB | Binlog_Ignore_DB | Executed_Gtid_Set |
+-------------------+----------+--------------+------------------+-------------------+
| master_bin.000003 |      343 |              |                  |                   |
+-------------------+----------+--------------+------------------+-------------------+
1 row in set (0.01 sec)
```

## 2.6 slave status

### 2.6.1 slave_1

```mysql
mysql> show databases;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| master_test_db     |
| mysql              |
| performance_schema |
| sys                |
+--------------------+
5 rows in set (0.01 sec)

mysql> show master status;
+--------------------+----------+--------------+------------------+-------------------+
| File               | Position | Binlog_Do_DB | Binlog_Ignore_DB | Executed_Gtid_Set |
+--------------------+----------+--------------+------------------+-------------------+
| slave_1_bin.000003 |      343 |              |                  |                   |
+--------------------+----------+--------------+------------------+-------------------+
1 row in set (0.00 sec)
```

```mysql
mysql> show slave status\G;
*************************** 1. row ***************************
               Slave_IO_State: Waiting for master to send event
                  Master_Host: mysql_master
                  Master_User: root
                  Master_Port: 3306
                Connect_Retry: 60
              Master_Log_File: master_bin.000003
          Read_Master_Log_Pos: 343
               Relay_Log_File: slave_1_relay_bin.000002
                Relay_Log_Pos: 510
        Relay_Master_Log_File: master_bin.000003
             Slave_IO_Running: Yes
            Slave_SQL_Running: Yes
              Replicate_Do_DB:
          Replicate_Ignore_DB:
           Replicate_Do_Table:
       Replicate_Ignore_Table:
      Replicate_Wild_Do_Table:
  Replicate_Wild_Ignore_Table:
                   Last_Errno: 0
                   Last_Error:
                 Skip_Counter: 0
          Exec_Master_Log_Pos: 343
              Relay_Log_Space: 719
              Until_Condition: None
               Until_Log_File:
                Until_Log_Pos: 0
           Master_SSL_Allowed: No
           Master_SSL_CA_File:
           Master_SSL_CA_Path:
              Master_SSL_Cert:
            Master_SSL_Cipher:
               Master_SSL_Key:
        Seconds_Behind_Master: 0
Master_SSL_Verify_Server_Cert: No
                Last_IO_Errno: 0
                Last_IO_Error:
               Last_SQL_Errno: 0
               Last_SQL_Error:
  Replicate_Ignore_Server_Ids:
             Master_Server_Id: 10
                  Master_UUID: 5471db9a-34b3-11eb-8fd1-0242ac1f0002
             Master_Info_File: /var/lib/mysql/master.info
                    SQL_Delay: 0
          SQL_Remaining_Delay: NULL
      Slave_SQL_Running_State: Slave has read all relay log; waiting for more updates
           Master_Retry_Count: 86400
                  Master_Bind:
      Last_IO_Error_Timestamp:
     Last_SQL_Error_Timestamp:
               Master_SSL_Crl:
           Master_SSL_Crlpath:
           Retrieved_Gtid_Set:
            Executed_Gtid_Set:
                Auto_Position: 0
         Replicate_Rewrite_DB:
                 Channel_Name:
           Master_TLS_Version:
1 row in set (0.00 sec)
```

### 2.6.2 slave_2

```mysql
mysql> show databases;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| master_test_db     |
| mysql              |
| performance_schema |
| sys                |
+--------------------+
5 rows in set (0.01 sec)

mysql> show master status;
+--------------------+----------+--------------+------------------+-------------------+
| File               | Position | Binlog_Do_DB | Binlog_Ignore_DB | Executed_Gtid_Set |
+--------------------+----------+--------------+------------------+-------------------+
| slave_2_bin.000003 |      343 |              |                  |                   |
+--------------------+----------+--------------+------------------+-------------------+
1 row in set (0.00 sec)
```

```mysql
mysql> show slave status\G;
*************************** 1. row ***************************
               Slave_IO_State: Waiting for master to send event
                  Master_Host: mysql_master
                  Master_User: root
                  Master_Port: 3306
                Connect_Retry: 60
              Master_Log_File: master_bin.000003
          Read_Master_Log_Pos: 343
               Relay_Log_File: slave_2_relay_bin.000002
                Relay_Log_Pos: 510
        Relay_Master_Log_File: master_bin.000003
             Slave_IO_Running: Yes
            Slave_SQL_Running: Yes
              Replicate_Do_DB:
          Replicate_Ignore_DB:
           Replicate_Do_Table:
       Replicate_Ignore_Table:
      Replicate_Wild_Do_Table:
  Replicate_Wild_Ignore_Table:
                   Last_Errno: 0
                   Last_Error:
                 Skip_Counter: 0
          Exec_Master_Log_Pos: 343
              Relay_Log_Space: 719
              Until_Condition: None
               Until_Log_File:
                Until_Log_Pos: 0
           Master_SSL_Allowed: No
           Master_SSL_CA_File:
           Master_SSL_CA_Path:
              Master_SSL_Cert:
            Master_SSL_Cipher:
               Master_SSL_Key:
        Seconds_Behind_Master: 0
Master_SSL_Verify_Server_Cert: No
                Last_IO_Errno: 0
                Last_IO_Error:
               Last_SQL_Errno: 0
               Last_SQL_Error:
  Replicate_Ignore_Server_Ids:
             Master_Server_Id: 10
                  Master_UUID: 5471db9a-34b3-11eb-8fd1-0242ac1f0002
             Master_Info_File: /var/lib/mysql/master.info
                    SQL_Delay: 0
          SQL_Remaining_Delay: NULL
      Slave_SQL_Running_State: Slave has read all relay log; waiting for more updates
           Master_Retry_Count: 86400
                  Master_Bind:
      Last_IO_Error_Timestamp:
     Last_SQL_Error_Timestamp:
               Master_SSL_Crl:
           Master_SSL_Crlpath:
           Retrieved_Gtid_Set:
            Executed_Gtid_Set:
                Auto_Position: 0
         Replicate_Rewrite_DB:
                 Channel_Name:
           Master_TLS_Version:
1 row in set (0.00 sec)
```

## 2.7 down & rmi

```bash
$ docker-compose down
Stopping mysql_replication_mysql_slave_2_1 ... done
Stopping mysql_replication_mysql_slave_1_1 ... done
Stopping mysql_replication_mysql_master_1  ... done
Removing mysql_replication_mysql_slave_2_1 ... done
Removing mysql_replication_mysql_slave_1_1 ... done
Removing mysql_replication_mysql_master_1  ... done
Removing network mysql_replication_docker_bridge
```

```bash
$ docker images
REPOSITORY                        TAG                 IMAGE ID            CREATED             SIZE
mysql_replication_mysql_slave_1   latest              c87cb498164a        16 minutes ago      449MB
mysql_replication_mysql_slave_2   latest              fb50987331eb        16 minutes ago      449MB
mysql_replication_mysql_master    latest              c40edb63fbff        16 minutes ago      449MB
mysql                             5.6                 e1b3da40572b        11 days ago         302MB
mysql                             5.7                 ae0658fdbad5        11 days ago         449MB
mysql                             latest              dd7265748b5d        11 days ago         545MB

$ docker rmi mysql_replication_mysql_master mysql_replication_mysql_slave_1 mysql_replication_mysql_slave_2
Untagged: mysql_replication_mysql_master:latest
Deleted: sha256:c40edb63fbff05e77ba895489c1c8af52a54eeb873970ac9eeecc3e8ca14e836
Deleted: sha256:b35ee2764212a46e1d6d0c614f6cc137b735cf43963b4b864722f96b309958b2
Untagged: mysql_replication_mysql_slave_1:latest
Deleted: sha256:c87cb498164a96499da1ed2a251d6f06519b893b42699a01aab95ca92ef1df4d
Deleted: sha256:40eac702b2a1fc6b4d95d61207ec264449fab019e608f6a50270830168c72730
Untagged: mysql_replication_mysql_slave_2:latest
Deleted: sha256:fb50987331eb8f963311d93ac4c1b309b81028e396f1d2fa7f76fe24c2375318
Deleted: sha256:8c6b5545b17cf5eaa6a5348958175900b6c55b6d58276077ffdaccfc60699cc7
```

```bash
$ docker network ls
NETWORK ID          NAME                DRIVER              SCOPE
11fbc12a2bfd        bridge              bridge              local
07b11c107114        host                host                local
b6feea96fd9b        none                null                local
```
