# 1. Procedure

```mysql
mysql> DELIMITER //
mysql> CREATE PROCEDURE load_orders (count INT UNSIGNED)
    -> BEGIN
    -> DECLARE i INT UNSIGNED DEFAULT 1;
    -> WHILE i <= count DO
    -> INSERT INTO `order` SELECT i,0,0,0,0,0,0,0,0,0;
    -> SET i=i+1;
    -> END WHILE;
    -> END;
    -> //
Query OK, 0 rows affected (0.00 sec)

mysql> DELIMITER ;
mysql> CALL load_orders(100000);
Query OK, 1 row affected (42.19 sec)
```

