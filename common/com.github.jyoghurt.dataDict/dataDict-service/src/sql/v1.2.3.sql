
ALTER TABLE `DataDictItem`
CHANGE COLUMN `operatorId` `founderId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人id' AFTER `modifyDateTime`,
CHANGE COLUMN `operatorName` `founderName`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人姓名' AFTER `founderId`,
ADD COLUMN `modifierId`  varchar(36) NULL COMMENT '修改人ID' AFTER `founderName`,
ADD COLUMN `modifierName`  varchar(20) NULL COMMENT '修改人姓名' AFTER `modifierId`;


ALTER TABLE `DataDictValue`
CHANGE COLUMN `operatorId` `founderId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人id' AFTER `modifyDateTime`,
CHANGE COLUMN `operatorName` `founderName`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人姓名' AFTER `founderId`,
ADD COLUMN `modifierId`  varchar(36) NULL COMMENT '修改人ID' AFTER `founderName`,
ADD COLUMN `modifierName`  varchar(20) NULL COMMENT '修改人姓名' AFTER `modifierId`;

