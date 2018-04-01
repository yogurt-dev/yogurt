CREATE TABLE `DataDictItem` (
  `dictItemId` varchar(36) NOT NULL COMMENT '主键',
  `dictItemCode` varchar(40) NOT NULL COMMENT '字典项编码',
  `dictItemName` varchar(40) DEFAULT NULL COMMENT '字典项名称',
  `dicItemDesc` varchar(100) DEFAULT NULL COMMENT '描述',
  `parentDictItemId` varchar(36) DEFAULT NULL COMMENT '父枚举项id',
  `uiConfigurable` tinyint(1) NOT NULL COMMENT '是否支持UI配置',
  `createDateTime` datetime DEFAULT NULL,
  `modifyDateTime` datetime DEFAULT NULL,
  `operatorId` varchar(36) DEFAULT NULL,
  `operatorName` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`dictItemId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `DataDictValue` (
  `dictValueId` varchar(36) NOT NULL COMMENT '主键',
  `dictItemCode` varchar(40) NOT NULL COMMENT '字典项编码',
  `dictValueCode` varchar(40) NOT NULL COMMENT '字典值编码',
  `dictValueName` longtext COMMENT '字典值名称',
  `dictValueDesc` varchar(100) DEFAULT NULL COMMENT '描述',
  `parentDictItemCode` varchar(36) DEFAULT NULL COMMENT '父字典项编码',
  `parentDictValueCode` varchar(36) DEFAULT NULL COMMENT '父字典值编码',
  `sortNum` int(11) DEFAULT NULL COMMENT '排序',
  `createDateTime` datetime DEFAULT NULL,
  `modifyDateTime` datetime DEFAULT NULL,
  `operatorId` varchar(36) DEFAULT NULL,
  `operatorName` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`dictValueId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

