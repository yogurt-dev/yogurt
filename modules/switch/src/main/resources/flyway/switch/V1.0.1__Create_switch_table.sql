CREATE TABLE if not exists `SwitchT` (
  `switchId` varchar(32) NOT NULL,
  `switchGroupKey` varchar(32) NOT NULL COMMENT '开关组key',
  `switchStatus` varchar(50) DEFAULT NULL COMMENT '开关状态',
  `availableTime` datetime NOT NULL COMMENT '生效时间',
  `remark` varchar(32) NOT NULL COMMENT '备注',
  `founderId` varchar(36) DEFAULT NULL COMMENT '创建人ID',
  `founderName` varchar(30) DEFAULT NULL COMMENT '创建人姓名',
  `modifierId` varchar(36) DEFAULT NULL COMMENT '修改人ID',
  `modifierName` varchar(30) DEFAULT NULL COMMENT '修改人姓名',
  `createDateTime` datetime NOT NULL COMMENT '创建时间',
  `modifyDateTime` datetime DEFAULT NULL COMMENT '修改时间',
  `deleteFlag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除 0 否 1是',
  PRIMARY KEY (`switchId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='开关表';

