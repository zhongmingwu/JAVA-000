USE e_commerce;
CREATE TABLE IF NOT EXISTS t_order_0 (id BIGINT NOT NULL PRIMARY KEY, user_id INT NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE t_order_1 LIKE t_order_0;