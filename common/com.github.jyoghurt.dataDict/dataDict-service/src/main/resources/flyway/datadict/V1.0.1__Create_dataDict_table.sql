CREATE TABLE IF NOT EXISTS `DataDictItem` (
  `dictItemId` varchar(36) NOT NULL COMMENT '主键',
  `dictItemCode` varchar(40) NOT NULL COMMENT '字典项编码',
  `dictItemName` varchar(40) DEFAULT NULL COMMENT '字典项名称',
  `dicItemDesc` varchar(100) DEFAULT NULL COMMENT '描述',
  `parentDictItemId` varchar(36) DEFAULT NULL COMMENT '父枚举项id',
  `uiConfigurable` tinyint(1) NOT NULL COMMENT '是否支持界面配置',
  `createDateTime` datetime DEFAULT NULL,
  `modifyDateTime` datetime DEFAULT NULL,
  `founderId` varchar(36) DEFAULT NULL COMMENT '创建人id',
  `founderName` varchar(20) DEFAULT NULL COMMENT '创建人姓名',
  `modifierId` varchar(36) DEFAULT NULL COMMENT '修改人ID',
  `modifierName` varchar(20) DEFAULT NULL COMMENT '修改人姓名',
  `singleFlag` varchar(1) DEFAULT '1' COMMENT '是否 为单独字典',
  `sortNum` int(11) DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`dictItemId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--ALTER TABLE `DataDictItem`
--ADD COLUMN `deleteFlag`  tinyint(1) NULL DEFAULT '0' COMMENT '删除标识';

--ALTER TABLE `DataDictItem`
--ADD COLUMN `parentDictItemCode` varchar(36) DEFAULT NULL after parentDictItemId;


CREATE TABLE IF NOT EXISTS `DataDictValue` (
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
  `founderId` varchar(36) DEFAULT NULL COMMENT '创建人id',
  `founderName` varchar(20) DEFAULT NULL COMMENT '创建人姓名',
  `modifierId` varchar(36) DEFAULT NULL COMMENT '修改人ID',
  `modifierName` varchar(20) DEFAULT NULL COMMENT '修改人姓名',
  PRIMARY KEY (`dictValueId`),
  KEY `dictItemCode` (`dictItemCode`,`dictValueCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--ALTER TABLE `DataDictValue`
--ADD COLUMN `deleteFlag`  tinyint(1) NULL DEFAULT '0' COMMENT '删除标识';




DELIMITER //
drop function if exists `findSubDictItemCodes` //
CREATE FUNCTION `findSubDictItemCodes`(rootId VARCHAR(1000)) RETURNS mediumtext CHARSET utf8mb4
BEGIN DECLARE pTemp text (100000); DECLARE cTemp text (100000);
SET pTemp = '$';
SET cTemp = rootId;
WHILE cTemp IS NOT NULL DO
	SET pTemp = CONCAT(pTemp, ',', cTemp);
	SELECT
		GROUP_CONCAT(dictItemCode) INTO cTemp
	FROM
		DataDictItem
	WHERE
		FIND_IN_SET(parentDictItemCode, cTemp) > 0;
END
WHILE;
RETURN pTemp;
END //

drop function if exists `getFatherNode` //
CREATE FUNCTION `getFatherNode`(rootId VARCHAR(50)) RETURNS mediumtext CHARSET utf8
BEGIN
       DECLARE pTemp text(100000);
       DECLARE cTemp text(100000);
       SET pTemp = '$';
       SET cTemp =cast(rootId as CHAR);
       WHILE cTemp is not null DO
         SET pTemp = concat(pTemp,',',cTemp);
         SELECT group_concat(parentId) INTO cTemp FROM SecurityUnitT
         WHERE FIND_IN_SET(unitId,cTemp)>0;
       END WHILE;
       RETURN pTemp;
END//
