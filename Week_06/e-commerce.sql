--
CREATE DATABASE IF NOT EXISTS `e_commerce` DEFAULT CHARACTER SET utf8mb4;
USE `e_commerce`;
--
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(32) NOT NULL,
    `avatar` VARCHAR(64) NOT NULL,
    `gender` TINYINT(1) NOT NULL,
    `status` TINYINT(1) NOT NULL,
    `creat_time` BIGINT(20) NOT NULL,
    `modify_time` BIGINT(20) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_ctime` (`creat_time`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;
--
CREATE TABLE IF NOT EXISTS `address` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT(20) NOT NULL,
    `location` VARCHAR(128) NOT NULL,
    `status` TINYINT(1) NOT NULL,
    `creat_time` BIGINT(20) NOT NULL,
    `modify_time` BIGINT(20) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_uid_ctime` (`user_id`, `creat_time`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;
--
CREATE TABLE IF NOT EXISTS `bank_card` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT(20) NOT NULL,
    `bank_name` VARCHAR(32) NOT NULL,
    `bank_number` VARCHAR(64) NOT NULL,
    `status` TINYINT(1) NOT NULL,
    `creat_time` BIGINT(20) NOT NULL,
    `modify_time` BIGINT(20) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_uid_ctime` (`user_id`, `creat_time`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;
--
CREATE TABLE IF NOT EXISTS `product` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(32) NOT NULL,
    `price` DECIMAL(10, 2) NOT NULL,
    `desc` VARCHAR(64) NOT NULL,
    `status` TINYINT(1) NOT NULL,
    `creat_time` BIGINT(20) NOT NULL,
    `modify_time` BIGINT(20) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_ctime` (`creat_time`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;
--
CREATE TABLE IF NOT EXISTS `order` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT(20) NOT NULL,
    `product_id` BIGINT(20) NOT NULL,
    `amount` DECIMAL(10, 2) NOT NULL,
    `total_price` DECIMAL(10, 2) NOT NULL,
    `address_id` BIGINT(20) NOT NULL,
    `bank_card_id` BIGINT(20) NOT NULL,
    `status` TINYINT(1) NOT NULL,
    `creat_time` BIGINT(20) NOT NULL,
    `modify_time` BIGINT(20) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_uid_pid_ctime` (`user_id`, `product_id`, `creat_time`),
    KEY `idx_uid_ctime` (`user_id`, `creat_time`),
    KEY `idx_pid_ctime` (`product_id`, `creat_time`),
    KEY `idx_ctime` (`creat_time`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;