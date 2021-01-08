# 作业1

## 描述
配置redis的主从复制，sentinel高可用，Cluster集群

## 解答

### 主从复制
docker-compose.yml
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

  redis_slave:
    image: redis
    container_name: redis_slave
    restart: always
    command: redis-server --slaveof redis_master 6379 --port 6379 --requirepass slave_pwd --masterauth master_pwd --appendonly yes
    ports:
      - 26379:6379
```
启动
```bash
$ docker-compose up
Creating network "master_slave_replication_default" with the default driver
Creating redis_slave  ... done
Creating redis_master ... done
Attaching to redis_slave, redis_master
redis_slave     | 1:C 08 Jan 2021 08:18:25.781 # oO0OoO0OoO0Oo Redis is starting oO0OoO0OoO0Oo
redis_slave     | 1:C 08 Jan 2021 08:18:25.781 # Redis version=6.0.9, bits=64, commit=00000000, modified=0, pid=1, just started
redis_slave     | 1:C 08 Jan 2021 08:18:25.781 # Configuration loaded
redis_slave     | 1:S 08 Jan 2021 08:18:25.784 * Running mode=standalone, port=6379.
redis_slave     | 1:S 08 Jan 2021 08:18:25.784 # WARNING: The TCP backlog setting of 511 cannot be enforced because /proc/sys/net/core/somaxconn is set to the lower value of 128.
redis_slave     | 1:S 08 Jan 2021 08:18:25.784 # Server initialized
redis_slave     | 1:S 08 Jan 2021 08:18:25.785 * Ready to accept connections
redis_slave     | 1:S 08 Jan 2021 08:18:25.785 * Connecting to MASTER redis_master:6379
redis_slave     | 1:S 08 Jan 2021 08:18:25.786 * MASTER <-> REPLICA sync started
redis_master    | 1:C 08 Jan 2021 08:18:25.879 # oO0OoO0OoO0Oo Redis is starting oO0OoO0OoO0Oo
redis_master    | 1:C 08 Jan 2021 08:18:25.879 # Redis version=6.0.9, bits=64, commit=00000000, modified=0, pid=1, just started
redis_master    | 1:C 08 Jan 2021 08:18:25.879 # Configuration loaded
redis_master    | 1:M 08 Jan 2021 08:18:25.882 * Running mode=standalone, port=6379.
redis_master    | 1:M 08 Jan 2021 08:18:25.883 # WARNING: The TCP backlog setting of 511 cannot be enforced because /proc/sys/net/core/somaxconn is set to the lower value of 128.
redis_master    | 1:M 08 Jan 2021 08:18:25.883 # Server initialized
redis_master    | 1:M 08 Jan 2021 08:18:25.884 * Ready to accept connections
redis_slave     | 1:S 08 Jan 2021 08:18:26.828 * Non blocking connect for SYNC fired the event.
redis_slave     | 1:S 08 Jan 2021 08:18:26.829 * Master replied to PING, replication can continue...
redis_slave     | 1:S 08 Jan 2021 08:18:26.831 * Partial resynchronization not possible (no cached master)
redis_master    | 1:M 08 Jan 2021 08:18:26.832 * Replica 172.22.0.2:6379 asks for synchronization
redis_master    | 1:M 08 Jan 2021 08:18:26.832 * Full resync requested by replica 172.22.0.2:6379
redis_master    | 1:M 08 Jan 2021 08:18:26.832 * Replication backlog created, my new replication IDs are 'f3cc582f4a0aac29864c25c2f2457725687f8d32' and '0000000000000000000000000000000000000000'
redis_master    | 1:M 08 Jan 2021 08:18:26.832 * Starting BGSAVE for SYNC with target: disk
redis_master    | 1:M 08 Jan 2021 08:18:26.833 * Background saving started by pid 20
redis_slave     | 1:S 08 Jan 2021 08:18:26.834 * Full resync from master: f3cc582f4a0aac29864c25c2f2457725687f8d32:0
redis_master    | 20:C 08 Jan 2021 08:18:26.837 * DB saved on disk
redis_master    | 20:C 08 Jan 2021 08:18:26.839 * RDB: 0 MB of memory used by copy-on-write
redis_master    | 1:M 08 Jan 2021 08:18:26.902 * Background saving terminated with success
redis_slave     | 1:S 08 Jan 2021 08:18:26.902 * MASTER <-> REPLICA sync: receiving 175 bytes from master to disk
redis_slave     | 1:S 08 Jan 2021 08:18:26.903 * MASTER <-> REPLICA sync: Flushing old data
redis_slave     | 1:S 08 Jan 2021 08:18:26.903 * MASTER <-> REPLICA sync: Loading DB in memory
redis_master    | 1:M 08 Jan 2021 08:18:26.902 * Synchronization with replica 172.22.0.2:6379 succeeded
redis_slave     | 1:S 08 Jan 2021 08:18:26.906 * Loading RDB produced by version 6.0.9
redis_slave     | 1:S 08 Jan 2021 08:18:26.906 * RDB age 0 seconds
redis_slave     | 1:S 08 Jan 2021 08:18:26.906 * RDB memory usage when created 1.83 Mb
redis_slave     | 1:S 08 Jan 2021 08:18:26.906 * MASTER <-> REPLICA sync: Finished with success
redis_slave     | 1:S 08 Jan 2021 08:18:26.907 * Background append only file rewriting started by pid 20
redis_slave     | 1:S 08 Jan 2021 08:18:26.937 * AOF rewrite child asks to stop sending diffs.
redis_slave     | 20:C 08 Jan 2021 08:18:26.937 * Parent agreed to stop sending diffs. Finalizing AOF...
redis_slave     | 20:C 08 Jan 2021 08:18:26.937 * Concatenating 0.00 MB of AOF diff received from parent.
redis_slave     | 20:C 08 Jan 2021 08:18:26.937 * SYNC append only file rewrite performed
redis_slave     | 20:C 08 Jan 2021 08:18:26.938 * AOF rewrite: 0 MB of memory used by copy-on-write
redis_slave     | 1:S 08 Jan 2021 08:18:27.002 * Background AOF rewrite terminated with success
redis_slave     | 1:S 08 Jan 2021 08:18:27.002 * Residual parent diff successfully flushed to the rewritten AOF (0.00 MB)
redis_slave     | 1:S 08 Jan 2021 08:18:27.002 * Background AOF rewrite finished successfully
```
master
```bash
$ redis-cli -h 127.0.0.1 -p 16379 -a master_pwd
Warning: Using a password with '-a' or '-u' option on the command line interface may not be safe.
127.0.0.1:16379> set name geek
OK
```
slave
```bash
$ redis-cli -h 127.0.0.1 -p 26379 -a slave_pwd
Warning: Using a password with '-a' or '-u' option on the command line interface may not be safe.
127.0.0.1:26379> get name
"geek"
127.0.0.1:26379> set name time
(error) READONLY You can't write against a read only replica.
```