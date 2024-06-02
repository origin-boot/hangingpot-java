CREATE DATABASE IF NOT EXISTS `hangingpot`;

USE `hangingpot`;

CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) NOT NULL,
  `password` varchar(20) NOT NULL,
  `create_time` int(11) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY(`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `users` VALUES('1', 'user1', 'password1', UNIX_TIMESTAMP());
INSERT INTO `users` VALUES('2', 'user2', 'password2', UNIX_TIMESTAMP());
INSERT INTO `users` VALUES('3', 'user3', 'password3', UNIX_TIMESTAMP());
