
ALTER TABLE `SecurityDataDic`
CHANGE COLUMN `operatorId` `founderId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人id' AFTER `modifyDateTime`,
CHANGE COLUMN `operatorName` `founderName`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人姓名' AFTER `founderId`,
ADD COLUMN `modifierId`  varchar(36) NULL COMMENT '修改人ID' AFTER `founderName`,
ADD COLUMN `modifierName`  varchar(20) NULL COMMENT '修改人姓名' AFTER `modifierId`;


ALTER TABLE `SecurityMenuRoleR`
CHANGE COLUMN `operatorId` `founderId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人id' AFTER `modifyDateTime`,
CHANGE COLUMN `operatorName` `founderName`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人姓名' AFTER `founderId`,
ADD COLUMN `modifierId`  varchar(36) NULL COMMENT '修改人ID' AFTER `founderName`,
ADD COLUMN `modifierName`  varchar(20) NULL COMMENT '修改人姓名' AFTER `modifierId`;

ALTER TABLE `SecurityMenuT`
CHANGE COLUMN `operatorId` `founderId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人id' AFTER `modifyDateTime`,
CHANGE COLUMN `operatorName` `founderName`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人姓名' AFTER `founderId`,
ADD COLUMN `modifierId`  varchar(36) NULL COMMENT '修改人ID' AFTER `founderName`,
ADD COLUMN `modifierName`  varchar(20) NULL COMMENT '修改人姓名' AFTER `modifierId`;

ALTER TABLE `SecurityRoleButtonR`
CHANGE COLUMN `operatorId` `founderId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人id' AFTER `modifyDateTime`,
CHANGE COLUMN `operatorName` `founderName`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人姓名' AFTER `founderId`,
ADD COLUMN `modifierId`  varchar(36) NULL COMMENT '修改人ID' AFTER `founderName`,
ADD COLUMN `modifierName`  varchar(20) NULL COMMENT '修改人姓名' AFTER `modifierId`;

ALTER TABLE `SecurityRoleT`
CHANGE COLUMN `operatorId` `founderId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人id' AFTER `modifyDateTime`,
CHANGE COLUMN `operatorName` `founderName`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人姓名' AFTER `founderId`,
ADD COLUMN `modifierId`  varchar(36) NULL COMMENT '修改人ID' AFTER `founderName`,
ADD COLUMN `modifierName`  varchar(20) NULL COMMENT '修改人姓名' AFTER `modifierId`;

ALTER TABLE `SecuritySyslogT`
CHANGE COLUMN `operatorId` `founderId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人id' AFTER `modifyDateTime`,
CHANGE COLUMN `operatorName` `founderName`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人姓名' AFTER `founderId`,
ADD COLUMN `modifierId`  varchar(36) NULL COMMENT '修改人ID' AFTER `founderName`,
ADD COLUMN `modifierName`  varchar(20) NULL COMMENT '修改人姓名' AFTER `modifierId`;

ALTER TABLE `SecurityUnitT`
CHANGE COLUMN `operatorId` `founderId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人id' AFTER `modifyDateTime`,
CHANGE COLUMN `operatorName` `founderName`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人姓名' AFTER `founderId`,
ADD COLUMN `modifierId`  varchar(36) NULL COMMENT '修改人ID' AFTER `founderName`,
ADD COLUMN `modifierName`  varchar(20) NULL COMMENT '修改人姓名' AFTER `modifierId`;

ALTER TABLE `SecurityUserRoleR`
CHANGE COLUMN `operatorId` `founderId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人id' AFTER `modifyDateTime`,
CHANGE COLUMN `operatorName` `founderName`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人姓名' AFTER `founderId`,
ADD COLUMN `modifierId`  varchar(36) NULL COMMENT '修改人ID' AFTER `founderName`,
ADD COLUMN `modifierName`  varchar(20) NULL COMMENT '修改人姓名' AFTER `modifierId`;

ALTER TABLE `SecurityUserT`
CHANGE COLUMN `operatorId` `founderId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人id' AFTER `modifyDateTime`,
CHANGE COLUMN `operatorName` `founderName`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人姓名' AFTER `founderId`,
ADD COLUMN `modifierId`  varchar(36) NULL COMMENT '修改人ID' AFTER `founderName`,
ADD COLUMN `modifierName`  varchar(20) NULL COMMENT '修改人姓名' AFTER `modifierId`;






