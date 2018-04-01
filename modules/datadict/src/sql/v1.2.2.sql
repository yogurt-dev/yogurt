ALTER TABLE `DataDictValue` DROP COLUMN `enabledFlag`;

ALTER TABLE `DataDictItem`
CHANGE COLUMN `enabledFlag` `uiConfigurable`  tinyint(1) NOT NULL COMMENT '是否支持界面配置' AFTER `parentDictItemId`;

ALTER TABLE `DataDictValue`
CHANGE COLUMN `dicValueName` `dictValueName`  longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '字典值名称' AFTER `dictValueCode`;



