CREATE DATABASE IF NOT EXISTS MonitorDB;
CREATE USER 'glimmer'@'localhost' IDENTIFIED BY '123456';
GRANT ALL PRIVILEGES ON MonitorDB . * TO 'glimmer'@'localhost';
FLUSH PRIVILEGES;
USE MonitorDB;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
   `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
   `name` varchar(20) COLLATE utf8_bin NOT NULL COMMENT '姓名',
   `passwd` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '密码',
   `role` int NOT NULL COMMENT '角色: 1.管理员 2.普通用户',
   `release_time` bigint DEFAULT 0 COMMENT '冻结期字段',
   `deadline_time` bigint NOT NULL COMMENT '有效期字段',
   `login_start` bigint NOT NULL COMMENT '开始登陆时间',
   `login_end` bigint NOT NULL COMMENT '结束登陆时间',
   `fail_times` int NOT NULL DEFAULT 0 COMMENT '连续登录失败次数',
   PRIMARY KEY (`id`),
   UNIQUE KEY `idx_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin COMMENT='用户信息';

INSERT INTO `user`(id,name, passwd, role, deadline_time, login_start, login_end) VALUES (0,'rootUser','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92',1,9223372036854775807,30900000000000,74100000000000);

DROP TABLE IF EXISTS `camera`;
CREATE TABLE `camera` (
    `ca_id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` varchar(20) COLLATE utf8_bin NOT NULL COMMENT '摄像头名称',
    `ip` varchar(30) COLLATE utf8_bin NOT NULL COMMENT '摄像头网络ip',
    `port` varchar(20) COLLATE utf8_bin NOT NULL COMMENT '摄像头网络port',
    `channel` bigint NOT NULL COMMENT '不同的channel意味着这台监控主机所显示的不同画面',
    `user` varchar(20) COLLATE utf8_bin NOT NULL COMMENT '用户名',
    `passwd` varchar(30) COLLATE utf8_bin NOT NULL COMMENT '密码',
    `area` varchar(40) COLLATE utf8_bin NOT NULL COMMENT '辖区',
    `start_time` bigint NOT NULL COMMENT '开始检测时间',
    `end_time` bigint NOT NULL COMMENT '结束检测时间',
    `infer_class` varchar(40) COLLATE utf8_bin NOT NULL COMMENT '摄像头检测类别列表',
    PRIMARY KEY (`ca_id`),
    UNIQUE KEY `idx_name` (`name`)

) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin COMMENT='摄像头信息';

DROP TABLE IF EXISTS `alert`;
CREATE TABLE `alert` (
          `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
          `ca_id` int NOT NULL COMMENT '摄像头id',
          `alert_time` bigint NOT NULL COMMENT '报警时间',
          `type` varchar(20) COLLATE utf8_bin NOT NULL COMMENT '报警类型',
          `path_video` varchar(150) COLLATE utf8_bin DEFAULT NULL COMMENT '报警录像路径',
          `path_photo` varchar(150) COLLATE utf8_bin NOT NULL COMMENT '报警照片路径',
          PRIMARY KEY (`id`)

) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin COMMENT='摄像头信息';

DROP TABLE IF EXISTS `box`;
CREATE TABLE `box` (
                         `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
                         `ca_id` int NOT NULL COMMENT '摄像头id',
                          `left_up` varchar(20) NOT NULL COMMENT '矩形框数据左上角的点所占整个图像的比例',
                          `right_down` varchar(20) NOT NULL COMMENT '矩形框数据右下角的点所占整个图像的比例',
                         PRIMARY KEY (`id`)

) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin COMMENT='摄像头信息';

