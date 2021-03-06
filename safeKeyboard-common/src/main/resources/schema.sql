DROP TABLE IF EXISTS `errors_messages`;
CREATE TABLE `errors_messages` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `locale` varchar(12) NOT NULL COMMENT '语言代码',
  `code` varchar(360)  NOT NULL COMMENT '错误码或异常名',
  `message` varchar(1200) NOT NULL COMMENT '错误信息',
  `systemCode` varchar(12) NOT NULL DEFAULT 'BASE' COMMENT '错误码来源系统',
  `type`	varchar(12) NOT NULL DEFAULT 'BIZ' COMMENT '错误码类型: BIZ-业务类;SYSTEM-系统类;',
  `createtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updatetime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `errors_messages_uk` (`locale`,`code`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='错误信息表';

DROP TABLE IF EXISTS `values_messages`;
CREATE TABLE `values_messages` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `locale` varchar(12) NOT NULL COMMENT '语言代码',
  `code` varchar(360)  NOT NULL COMMENT '资源代码',
  `message` varchar(1200) NOT NULL COMMENT '文本信息',
  `createtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updatetime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `values_messages_uk` (`locale`,`code`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='资源文本表';

DROP TABLE IF EXISTS `errors_count`;
CREATE TABLE `errors_count` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `traceNo` varchar(40) NOT NULL COMMENT '错误追踪号',
  `code` varchar(360)  NOT NULL COMMENT '错误码',
  `message` varchar(1200) NOT NULL COMMENT '错误信息',
  `cause` varchar(1200)  COMMENT '原错误信息',
  `hostName` varchar(100)  COMMENT '发生错误的主机',
  `helpLink` varchar(100)  COMMENT '帮助链接',
  `path` varchar(100)  COMMENT '请求URI',
  `refersTo` varchar(100)  COMMENT '信息所涉及的项目',
  `severity` varchar(30)  COMMENT 'Severity of the message: Success, Warning, Error and Exception',
  `createtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updatetime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `errors_count_traceNo` (`traceNo`),
  KEY `errors_count_code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='错误信息统计表';
