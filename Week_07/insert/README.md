# 0 Summary

| Type          | Elapsed Ms |
| ------------- | ---------- |
| Procedure     | 33540      |
| Single Insert | 21843      |
| Batch Insert  | 2511       |

# 1 Procedure

```mysql
mysql> DELIMITER //
mysql> CREATE PROCEDURE load_orders (count INT UNSIGNED)
    -> BEGIN
    -> DECLARE i INT UNSIGNED DEFAULT 1;
    -> WHILE i <= count DO
    -> INSERT INTO `order` SELECT null,i,0,0,0,0,0,0,0,0;
    -> SET i=i+1;
    -> END WHILE;
    -> END;
    -> //
Query OK, 0 rows affected (0.00 sec)

mysql> DELIMITER ;
mysql> CALL load_orders(100000);
Query OK, 1 row affected (33.54 sec)
```

# 2 Single Insert &Batch Insert

[source code](https://github.com/zhongmingwu/java-training-camp/tree/main/week07/insert)

```java
package time.geekbang.org.insert;

import com.google.common.base.Stopwatch;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootTest
class InsertApplicationTests {

    private static final int COUNT = 100_000;
    private static final int BATCH_SIZE = 10_000;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final Stopwatch stopwatch = Stopwatch.createUnstarted();

    @BeforeEach
    public void setup() {
        stopwatch.start();
    }

    @AfterEach
    public void destroy() {
        stopwatch.stop();
        log.info("take {}ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        stopwatch.reset();
    }

    @Test
    @Transactional
    public void singleInsertTest() {
        for (int i = 0; i < COUNT; i++) {
            jdbcTemplate.execute(String.format("insert into `order` values (null,%s,0,0,0,0,0,0,0,0)", i));
        }
    }

    @Test
    @Transactional
    public void batchInsertTest() {
        String baseSql = "insert into `order` values ";
        StringBuilder builder = new StringBuilder(baseSql);
        for (int i = 0; i < COUNT; i++) {
            builder.append(String.format("(null,%s,0,0,0,0,0,0,0,0),", i));
            if ((i + 1) % BATCH_SIZE == 0) {
                doInsert(builder);
                builder = new StringBuilder(baseSql);
            }
        }

        if (!builder.toString().equals(baseSql)) {
            doInsert(builder);
        }
    }

    private void doInsert(@NonNull StringBuilder builder) {
        builder.deleteCharAt(builder.lastIndexOf(","));
        jdbcTemplate.execute(builder.toString());
    }
}
```

