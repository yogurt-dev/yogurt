CREATE TABLE `SecurityUserUnitR` (
  `userUnitId` varchar(36) CHARACTER SET utf8 NOT NULL COMMENT '关系ID',
  `userIdR` varchar(36) CHARACTER SET utf8 DEFAULT NULL COMMENT '用户ID',
  `unitIdR` varchar(36) CHARACTER SET utf8 DEFAULT NULL COMMENT '组织机构ID',
  `userNameR` varchar(30) CHARACTER SET utf8 DEFAULT NULL COMMENT '用户名',
  `unitNameR` varchar(30) CHARACTER SET utf8 DEFAULT NULL COMMENT '组织机构名称',
  `parentUnitId` varchar(36) CHARACTER SET utf8 DEFAULT NULL COMMENT '上级单位ID',
  `parentUnitName` varchar(30) CHARACTER SET utf8 DEFAULT NULL COMMENT '上级单位名称',
  `founderId` varchar(36) NOT NULL DEFAULT '' COMMENT '创建人ID',
  `founderName` varchar(30) NOT NULL DEFAULT '' COMMENT '创建人姓名',
  `modifierId` varchar(36) NOT NULL DEFAULT '' COMMENT '修改人ID',
  `modifierName` varchar(30) NOT NULL DEFAULT '' COMMENT '修改人姓名',
  `createDateTime` datetime NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '创建时间',
  `modifyDateTime` datetime NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '修改时间',
  `deleteFlag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除 0 否 1是',
  PRIMARY KEY (`userUnitId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

