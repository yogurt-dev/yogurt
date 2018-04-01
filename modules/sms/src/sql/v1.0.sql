/*
Navicat MySQL Data Transfer

Source Server         : 115.28.2.160_9981_cxm
Source Server Version : 50626
Source Host           : 115.28.2.160:9981
Source Database       : cxm

Target Server Type    : MYSQL
Target Server Version : 50626
File Encoding         : 65001

Date: 2015-12-28 09:17:42
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `DataDictItem`
-- ----------------------------
DROP TABLE IF EXISTS `DataDictItem`;
CREATE TABLE `DataDictItem` (
  `dictItemId` varchar(36) NOT NULL COMMENT '主键',
  `dictItemCode` varchar(40) NOT NULL COMMENT '字典项编码',
  `dictItemName` varchar(40) DEFAULT NULL COMMENT '字典项名称',
  `dicItemDesc` varchar(100) DEFAULT NULL COMMENT '描述',
  `parentDictItemId` varchar(36) DEFAULT NULL COMMENT '父枚举项id',
  `uiConfigurable` tinyint(1) NOT NULL COMMENT '是否支持界面配置',
  `createDateTime` datetime DEFAULT NULL,
  `modifyDateTime` datetime DEFAULT NULL,
  `operatorId` varchar(36) DEFAULT NULL,
  `operatorName` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`dictItemId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of DataDictItem
-- ----------------------------
INSERT INTO `DataDictItem` VALUES ('1', 'SmsConfig', '短信配置', '短信配置', null, '1', '2015-12-25 10:29:30', '2015-12-25 10:29:35', null, null);

-- ----------------------------
-- Table structure for `DataDictValue`
-- ----------------------------
DROP TABLE IF EXISTS `DataDictValue`;
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

-- ----------------------------
-- Records of DataDictValue
-- ----------------------------
INSERT INTO `DataDictValue` VALUES ('1', 'SmsConfig', 'serverIP', 'sandboxapp.cloopen.com', '服务器地址', '', null, '1', null, null, null, null);
INSERT INTO `DataDictValue` VALUES ('2', 'SmsConfig', 'serverPort', '8883', '服务器端口', '', null, '2', null, null, null, null);
INSERT INTO `DataDictValue` VALUES ('3', 'SmsConfig', 'accountSid', '8a48b5515124598801513834f9cd3d4e', '主账号', '', null, '3', null, null, null, null);
INSERT INTO `DataDictValue` VALUES ('4', 'SmsConfig', 'accountToken', '79c55209ea144451b6dba59c45eead4e', '主账号令牌', '', null, '4', null, null, null, null);
INSERT INTO `DataDictValue` VALUES ('5', 'SmsConfig', 'appId', 'aaf98f89512446e201513835b8f33c67', '初始化应用ID ', '', null, '5', null, null, null, null);
INSERT INTO `DataDictValue` VALUES ('6', 'SmsConfig', 'regulationTemplateId', '1', '短信模板', '', null, '6', null, null, null, null);
