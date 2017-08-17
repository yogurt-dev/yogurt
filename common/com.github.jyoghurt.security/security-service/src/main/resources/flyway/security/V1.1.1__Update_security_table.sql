DELIMITER //
CREATE PROCEDURE `PRC_CREATE_MONTH_LOG_TABLE`()
BEGIN
		DECLARE _exception INT DEFAULT 0;
		DECLARE CONTINUE HANDLER FOR SQLEXCEPTION  SET _exception = 1;
		SET time_zone = '+8:00';
		SET @logTempTableName=date_format(adddate(now(),+30),'%Y%m');
		SET @logTempSqlstr=CONCAT("CREATE TABLE if not exists `SecuritySyslogT",@logTempTableName," ` (
			`logId` varchar(36) NOT NULL COMMENT '日志ID',
			`ipAddress` varchar(128) DEFAULT NULL COMMENT 'IP地址',
			`moduleName` varchar(20) DEFAULT NULL COMMENT '模块名称',
			`state` int(1) DEFAULT NULL COMMENT '状态',
			`logMessage` varchar(100) DEFAULT NULL COMMENT '日志内容',
			`errorMessage` varchar(10000) DEFAULT NULL COMMENT '异常信息',
			`classMethodName` varchar(500) DEFAULT NULL COMMENT '类方法名',
			`methodParameterValues` longtext CHARACTER SET utf8mb4 COMMENT '参数列表',
			`systemType` varchar(20) DEFAULT NULL COMMENT '系统类型',
			`clientType` varchar(20) DEFAULT NULL COMMENT '客户端类型',
			`invokeDuration` bigint(20) DEFAULT NULL COMMENT '调用时长',
			`createDateTime` datetime DEFAULT NULL COMMENT '操作时间',
			`modifyDateTime` datetime DEFAULT NULL,
			`founderId` varchar(36) DEFAULT NULL COMMENT '创建人id',
			`founderName` varchar(20) DEFAULT NULL COMMENT '创建人姓名',
			`modifierId` varchar(36) DEFAULT NULL COMMENT '修改人ID',
			`modifierName` varchar(20) DEFAULT NULL COMMENT '修改人姓名',
			`abnormalLogContent` varchar(100) DEFAULT NULL COMMENT '违法访问判断标识',
			PRIMARY KEY (`logId`),
			KEY `FK_Reference_8` (`founderId`) USING BTREE,
			KEY `SecuritySyslogT_index_logMessage` (`logMessage`)
		) ENGINE=InnoDB DEFAULT CHARSET=utf8;");

		PREPARE stmt FROM @logTempSqlstr;
    EXECUTE stmt;
END//
DELIMITER ;