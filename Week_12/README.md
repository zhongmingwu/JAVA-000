# 作业1

## 描述
配置redis的主从复制，sentinel高可用，Cluster集群

## 解答

### 主从复制

#### docker-compose.yml
```yml
version: "3.6"
services:
  redis_master:
    image: redis
    container_name: redis_master
    restart: always
    command: redis-server --port 6379 --requirepass master_pwd --appendonly yes
    ports:
      - 16379:6379

  redis_slave_1:
    image: redis
    container_name: redis_slave_1
    restart: always
    command: redis-server --slaveof redis_master 6379 --port 6379 --requirepass slave_pwd --masterauth master_pwd --appendonly yes
    ports:
      - 26379:6379

  redis_slave_2:
    image: redis
    container_name: redis_slave_2
    restart: always
    command: redis-server --slaveof redis_master 6379 --port 6379 --requirepass slave_pwd --masterauth master_pwd --appendonly yes
    ports:
      - 36379:6379
```

#### 启动
```bash
$ docker-compose up -d
Creating network "master_slave_replication_default" with the default driver
Creating redis_master  ... done
Creating redis_slave_1 ... done
Creating redis_slave_2 ... done

$ docker-compose ps -a
    Name                   Command               State            Ports
--------------------------------------------------------------------------------
redis_master    docker-entrypoint.sh redis ...   Up      0.0.0.0:16379->6379/tcp
redis_slave_1   docker-entrypoint.sh redis ...   Up      0.0.0.0:26379->6379/tcp
redis_slave_2   docker-entrypoint.sh redis ...   Up      0.0.0.0:36379->6379/tcp
```

#### master

```bash
$ redis-cli -h 127.0.0.1 -p 16379 -a master_pwd
Warning: Using a password with '-a' or '-u' option on the command line interface may not be safe.
127.0.0.1:16379> info Replication
# Replication
role:master
connected_slaves:2
slave0:ip=172.24.0.4,port=6379,state=online,offset=168,lag=0
slave1:ip=172.24.0.3,port=6379,state=online,offset=168,lag=0
master_replid:5a3e236f1517f85f59a5a18b07fe1b4c799bc5e0
master_replid2:0000000000000000000000000000000000000000
master_repl_offset:168
second_repl_offset:-1
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:1
repl_backlog_histlen:168
127.0.0.1:16379> set name geek
OK
```

#### slave

```bash
$ redis-cli -h 127.0.0.1 -p 26379 -a slave_pwd
Warning: Using a password with '-a' or '-u' option on the command line interface may not be safe.
127.0.0.1:26379> info Replication
# Replication
role:slave
master_host:redis_master
master_port:6379
master_link_status:up
master_last_io_seconds_ago:5
master_sync_in_progress:0
slave_repl_offset:294
slave_priority:100
slave_read_only:1
connected_slaves:0
master_replid:5a3e236f1517f85f59a5a18b07fe1b4c799bc5e0
master_replid2:0000000000000000000000000000000000000000
master_repl_offset:294
second_repl_offset:-1
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:1
repl_backlog_histlen:294
127.0.0.1:26379> get name
"geek"
127.0.0.1:26379> set name time
(error) READONLY You can't write against a read only replica.
```

### Sentinel

#### docker-compose.yml

```yaml
version: "3.6"
services:
  redis_master:
    image: redis
    container_name: redis_master
    restart: always
    command: redis-server --port 6379 --requirepass master_pwd --appendonly yes
    ports:
      - 16379:6379

  redis_slave_1:
    image: redis
    container_name: redis_slave_1
    restart: always
    command: redis-server --slaveof redis_master 6379 --port 6379 --requirepass slave_pwd --masterauth master_pwd --appendonly yes
    ports:
      - 26379:6379

  redis_slave_2:
    image: redis
    container_name: redis_slave_2
    restart: always
    command: redis-server --slaveof redis_master 6379 --port 6379 --requirepass slave_pwd --masterauth master_pwd --appendonly yes
    ports:
      - 36379:6379

  redis_sentinel_1:
    image: redis
    container_name: redis_sentinel_1
    restart: always
    ports:
      - 10000:6379
    command: redis-sentinel /usr/local/etc/redis/sentinel.conf
    volumes:
      - ./sentinel.conf:/usr/local/etc/redis/sentinel.conf

  redis_sentinel_2:
    image: redis
    container_name: redis_sentinel_2
    restart: always
    ports:
      - 20000:6379
    command: redis-sentinel /usr/local/etc/redis/sentinel.conf
    volumes:
      - ./sentinel.conf:/usr/local/etc/redis/sentinel.conf

  redis_sentinel_3:
    image: redis
    container_name: redis_sentinel_3
    restart: always
    ports:
      - 30000:6379
    command: redis-sentinel /usr/local/etc/redis/sentinel.conf
    volumes:
      - ./sentinel.conf:/usr/local/etc/redis/sentinel.conf
```

#### sentinel.conf

```
port 6379
sentinel monitor mymaster redis_master 6379 2
sentinel auth-pass mymaster master_pwd
sentinel down-after-milliseconds mymaster 30000
sentinel parallel-syncs mymaster 1
sentinel failover-timeout mymaster 60000
sentinel deny-scripts-reconfig yes
```

#### 启动

```bash
$ docker-compose up -d
Creating network "sentinel_default" with the default driver
Creating redis_sentinel_1 ... done
Creating redis_master     ... done
Creating redis_slave_1    ... done
Creating redis_sentinel_3 ... done
Creating redis_sentinel_2 ... done
Creating redis_slave_2    ... done

$ docker-compose ps -a
      Name                    Command               State            Ports
-----------------------------------------------------------------------------------
redis_master       docker-entrypoint.sh redis ...   Up      0.0.0.0:16379->6379/tcp
redis_sentinel_1   docker-entrypoint.sh redis ...   Up      0.0.0.0:10000->6379/tcp
redis_sentinel_2   docker-entrypoint.sh redis ...   Up      0.0.0.0:20000->6379/tcp
redis_sentinel_3   docker-entrypoint.sh redis ...   Up      0.0.0.0:30000->6379/tcp
redis_slave_1      docker-entrypoint.sh redis ...   Up      0.0.0.0:26379->6379/tcp
redis_slave_2      docker-entrypoint.sh redis ...   Up      0.0.0.0:36379->6379/tcp
```

#### sentinel

```bash
$ redis-cli -h 127.0.0.1 -p 10000
127.0.0.1:10000> info Sentinel
# Sentinel
sentinel_masters:1
sentinel_tilt:0
sentinel_running_scripts:0
sentinel_scripts_queue_length:0
sentinel_simulate_failure_flags:0
master0:name=mymaster,status=ok,address=172.30.0.5:6379,slaves=2,sentinels=3
```

