# 建表
```sql
CREATE TABLE IF NOT EXISTS ` order ` (
    ` id ` BIGINT(20) NOT NULL PRIMARY KEY,
    ` user_id ` BIGINT(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```