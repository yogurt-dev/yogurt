CREATE TABLE `ImgT` (
  `imgId` varchar(36) NOT NULL COMMENT '图片ID',
  `name` varchar(100) DEFAULT NULL COMMENT '图片名称',
  `naturalkey` varchar(36) DEFAULT NULL COMMENT '业务主键',
  `path` varchar(500) DEFAULT NULL COMMENT '存储路径',
  `imgDesc` varchar(200) DEFAULT NULL COMMENT '图片描述',
  `type` varchar(20) DEFAULT NULL COMMENT '类型',
  `founderId` varchar(36) NOT NULL DEFAULT '' COMMENT '创建人ID',
  `founderName` varchar(30) NOT NULL DEFAULT '' COMMENT '创建人姓名',
  `modifierId` varchar(36) NOT NULL DEFAULT '' COMMENT '修改人ID',
  `modifierName` varchar(30) NOT NULL DEFAULT '' COMMENT '修改人姓名',
  `createDateTime` datetime NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '创建时间',
  `modifyDateTime` datetime NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '修改时间',
  `deleteFlag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除 0 否 1是',
  PRIMARY KEY (`imgId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='图片表';

