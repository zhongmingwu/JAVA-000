# 1 Files

```bash
$ tree
.
├── README.md
├── docker-compose.yml
├── master
│   ├── Dockerfile
│   └── my.cnf
└── slave
    ├── Dockerfile
    └── my.cnf

2 directories, 6 files
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

  mysql_slave:
    build:
      context: .
      dockerfile: slave/Dockerfile
    environment:
      - "MYSQL_ROOT_PASSWORD=root"
    ports:
      - "23306:3306"
    restart: always
    hostname: mysql_slave
    networks:
      - docker_bridge

networks:
  docker_bridge:
    driver: bridge
```

## 1.2 master/Dockerfile

```dockerfile
FROM mysql:5.7
ADD ./master/my.cnf /etc/mysql/my.cnf
```

## 1.3 master/my.cnf

```cnf
[mysqld]
server_id=1
log-bin=master_bin
```

## 1.4 slave/Dockerfile

```dockerfile
FROM mysql:5.7
ADD ./slave/my.cnf /etc/mysql/my.cnf
```

## 1.5 slave/my.cnf

```cnf
[mysqld]
server_id=2
log-bin=slave_bin
relay_log=slave_relay_bin
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
 ---> aca797b87d2d

Successfully built aca797b87d2d
Successfully tagged mysql_replication_mysql_master:latest
Building mysql_slave
Step 1/2 : FROM mysql:5.7
 ---> ae0658fdbad5
Step 2/2 : ADD ./slave/my.cnf /etc/mysql/my.cnf
 ---> 32c306b1afee

Successfully built 32c306b1afee
Successfully tagged mysql_replication_mysql_slave:latest
Creating mysql_replication_mysql_slave_1  ... done
Creating mysql_replication_mysql_master_1 ... done
```

```bash
$ docker-compose ps -a
              Name                           Command             State                 Ports
-----------------------------------------------------------------------------------------------------------
mysql_replication_mysql_master_1   docker-entrypoint.sh mysqld   Up      0.0.0.0:13306->3306/tcp, 33060/tcp
mysql_replication_mysql_slave_1    docker-entrypoint.sh mysqld   Up      0.0.0.0:23306->3306/tcp, 33060/tcp
```

## 2.2 Network

```bash
$ docker network ls
NETWORK ID          NAME                              DRIVER              SCOPE
11fbc12a2bfd        bridge                            bridge              local
07b11c107114        host                              host                local
fdde7e7f1956        mysql_replication_docker_bridge   bridge              local
b6feea96fd9b        none                              null                local
```

```bash
$ docker inspect mysql_replication_docker_bridge
[
    {
        "Name": "mysql_replication_docker_bridge",
        "Id": "fdde7e7f1956f0bf8da61ff26430622f04ba0e79db316ccb9769f36beaff6c66",
        "Created": "2020-12-02T08:44:47.6879786Z",
        "Scope": "local",
        "Driver": "bridge",
        "EnableIPv6": false,
        "IPAM": {
            "Driver": "default",
            "Options": null,
            "Config": [
                {
                    "Subnet": "172.29.0.0/16",
                    "Gateway": "172.29.0.1"
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
            "074c510fce929b4d91946b5071698c9f3c399d26a8209ada18aa51bf33e36a18": {
                "Name": "mysql_replication_mysql_slave_1",
                "EndpointID": "62e4cc05531d0aa4c13740337695324ea528501f9d3fd781445e00041c67eed0",
                "MacAddress": "02:42:ac:1d:00:02",
                "IPv4Address": "172.29.0.2/16",
                "IPv6Address": ""
            },
            "bd02031398584ee1730f4a44138d0c6fd73bfc0af8babcab4fec20adf0818bca": {
                "Name": "mysql_replication_mysql_master_1",
                "EndpointID": "a60a0510cbc1efdd34b61dca13ce67c5eceebed40d3b91f2b30315234bec795c",
                "MacAddress": "02:42:ac:1d:00:03",
                "IPv4Address": "172.29.0.3/16",
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

## 2.3 mysql_master : show master status

```bash
$ mysql -h127.0.0.1 -P13306 -uroot -proot
mysql: [Warning] Using a password on the command line interface can be insecure.
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 2
Server version: 5.7.32-log MySQL Community Server (GPL)

Copyright (c) 2000, 2020, Oracle and/or its affiliates. All rights reserved.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> show master status;
+-------------------+----------+--------------+------------------+-------------------+
| File              | Position | Binlog_Do_DB | Binlog_Ignore_DB | Executed_Gtid_Set |
+-------------------+----------+--------------+------------------+-------------------+
| master_bin.000003 |      154 |              |                  |                   |
+-------------------+----------+--------------+------------------+-------------------+
1 row in set (0.00 sec)

mysql> show databases;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| mysql              |
| performance_schema |
| sys                |
+--------------------+
4 rows in set (0.02 sec)
```

## 2.4 mysql_slave : start slave

```bash
$ mysql -h127.0.0.1 -P23306 -uroot -proot
mysql: [Warning] Using a password on the command line interface can be insecure.
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 3
Server version: 5.7.32-log MySQL Community Server (GPL)

Copyright (c) 2000, 2020, Oracle and/or its affiliates. All rights reserved.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> show master status;
+------------------+----------+--------------+------------------+-------------------+
| File             | Position | Binlog_Do_DB | Binlog_Ignore_DB | Executed_Gtid_Set |
+------------------+----------+--------------+------------------+-------------------+
| slave_bin.000003 |      154 |              |                  |                   |
+------------------+----------+--------------+------------------+-------------------+
1 row in set (0.00 sec)

mysql> show slave status;
Empty set (0.01 sec)

mysql> change master to master_host='mysql_master',master_user='root',master_password='root',master_log_file='master_bin.000003',master_log_pos=154;
Query OK, 0 rows affected, 1 warning (0.03 sec)

mysql> start slave;
Query OK, 0 rows affected (0.00 sec)

mysql> show slave status\G;
*************************** 1. row ***************************
               Slave_IO_State: Waiting for master to send event
                  Master_Host: mysql_master
                  Master_User: root
                  Master_Port: 3306
                Connect_Retry: 60
              Master_Log_File: master_bin.000003
          Read_Master_Log_Pos: 154
               Relay_Log_File: slave_relay_bin.000002
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
              Relay_Log_Space: 528
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
             Master_Server_Id: 1
                  Master_UUID: a4af4717-347a-11eb-8b95-0242ac1d0003
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

ERROR:
No query specified
```

## 2.5 mysql_master : create database

```sql
mysql> create database master_test_db;
Query OK, 1 row affected (0.01 sec)

mysql> show master status;
+-------------------+----------+--------------+------------------+-------------------+
| File              | Position | Binlog_Do_DB | Binlog_Ignore_DB | Executed_Gtid_Set |
+-------------------+----------+--------------+------------------+-------------------+
| master_bin.000003 |      343 |              |                  |                   |
+-------------------+----------+--------------+------------------+-------------------+
1 row in set (0.01 sec)
```

## 2.6 mysql_slave : show slave status

```sql
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
+------------------+----------+--------------+------------------+-------------------+
| File             | Position | Binlog_Do_DB | Binlog_Ignore_DB | Executed_Gtid_Set |
+------------------+----------+--------------+------------------+-------------------+
| slave_bin.000003 |      343 |              |                  |                   |
+------------------+----------+--------------+------------------+-------------------+
1 row in set (0.00 sec)

mysql> show slave status\G;
*************************** 1. row ***************************
               Slave_IO_State: Waiting for master to send event
                  Master_Host: mysql_master
                  Master_User: root
                  Master_Port: 3306
                Connect_Retry: 60
              Master_Log_File: master_bin.000003
          Read_Master_Log_Pos: 343
               Relay_Log_File: slave_relay_bin.000002
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
              Relay_Log_Space: 717
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
             Master_Server_Id: 1
                  Master_UUID: a4af4717-347a-11eb-8b95-0242ac1d0003
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

ERROR:
No query specified
```

## 2.7 down & rmi

```bash
$ docker-compose down
Stopping mysql_replication_mysql_master_1 ... done
Stopping mysql_replication_mysql_slave_1  ... done
Removing mysql_replication_mysql_master_1 ... done
Removing mysql_replication_mysql_slave_1  ... done
Removing network mysql_replication_docker_bridge
```

```bash
$ docker images
REPOSITORY                       TAG                 IMAGE ID            CREATED             SIZE
mysql_replication_mysql_master   latest              aca797b87d2d        18 minutes ago      449MB
mysql_replication_mysql_slave    latest              32c306b1afee        18 minutes ago      449MB
mysql                            5.6                 e1b3da40572b        11 days ago         302MB
mysql                            5.7                 ae0658fdbad5        11 days ago         449MB
mysql                            latest              dd7265748b5d        11 days ago         545MB

$ docker rmi mysql_replication_mysql_master mysql_replication_mysql_slave
Untagged: mysql_replication_mysql_master:latest
Deleted: sha256:aca797b87d2dcc3f68e1c390de6d7d13c1f6bd24a6b0a0ba010eb8720d6dc321
Deleted: sha256:012591338067e825c1f8a7c90a7d4d05339720a9c5b1f61452afdee5d5ce44d7
Untagged: mysql_replication_mysql_slave:latest
Deleted: sha256:32c306b1afee7737f4b29caf9e661b0752aef9526b02f2753a2814dc5ca76000
Deleted: sha256:45318aeb4cef913f8848244bb0e5a61a78336074e092beeb7db9b95e21abee07
```

```bash
$ docker network ls
NETWORK ID          NAME                DRIVER              SCOPE
11fbc12a2bfd        bridge              bridge              local
07b11c107114        host                host                local
b6feea96fd9b        none                null                local
```

