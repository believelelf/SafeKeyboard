-- 用户-交易密码表
DROP TABLE IF EXISTS `user_password`;
CREATE TABLE `user_password` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `userid` varchar(40)  NOT NULL COMMENT '用户ID',
  `password` varchar(512) NOT NULL COMMENT '交易密码',
  `status`	  char(2) NOT NULL DEFAULT '00' COMMENT '密码状态：00-状态正常；01-状态异常',
  `verifydate`	date DEFAULT NULL COMMENT '密码核验日期',
  `errortimes` int(11) NOT NULL DEFAULT '0' COMMENT '当日密码校验次数',
  `createtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updatetime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_password_userid` (`userid`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COMMENT='用户-交易密码表';