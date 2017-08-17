/*
Navicat MySQL Data Transfer

Source Server         : 车险系统数据库
Source Server Version : 50626
Source Host           : 115.28.2.160:9981
Source Database       : cxm

Target Server Type    : MYSQL
Target Server Version : 50626
File Encoding         : 65001

Date: 2016-02-05 15:57:32
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for SecurityOnlineT
-- ----------------------------
DROP TABLE IF EXISTS `SecurityOnlineT`;
CREATE TABLE `SecurityOnlineT` (
  `uuid` varchar(36) CHARACTER SET utf8 NOT NULL COMMENT '主键',
  `uri` varchar(100) CHARACTER SET utf8 DEFAULT NULL COMMENT 'uri',
  `sessionId` varchar(100) CHARACTER SET utf8 DEFAULT NULL COMMENT 'sessionId',
  `localAddress` varchar(100) CHARACTER SET utf8 DEFAULT NULL COMMENT 'localAddress',
  `remoteAddress` varchar(100) CHARACTER SET utf8 DEFAULT NULL COMMENT 'remoteAddress',
  `createDateTime` datetime DEFAULT NULL COMMENT '创建时间',
  `modifyDateTime` datetime DEFAULT NULL COMMENT '修改时间',
  `founderId` varchar(36) CHARACTER SET utf8 DEFAULT NULL COMMENT '创建人ID',
  `founderName` varchar(20) CHARACTER SET utf8 DEFAULT NULL COMMENT '创建人名称',
  `modifierId` varchar(36) CHARACTER SET utf8 DEFAULT NULL COMMENT '修改人ID',
  `modifierName` varchar(20) CHARACTER SET utf8 DEFAULT NULL COMMENT '修改人ID',
  `bsflag` varchar(1) CHARACTER SET utf8 DEFAULT NULL COMMENT '是否删除 0 否 1是',
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

alter table SecuritySyslogT add abnormalLogContent varchar(100) COMMENT '违法访问判断标识';
--增加在綫統計菜單
-- INSERT INTO `SecurityMenuT` (`menuId`, `parentId`, `menuName`, `menuUrl`, `isLeaf`, `createDateTime`, `modifyDateTime`, `icon`, `sortId`, `founderId`, `founderName`, `modifierId`, `modifierName`) VALUES ('0f0aed45-7079-48ca-a912-3c7f86a1fb85', '11', '在线统计', '/onlineManage/SecurityOnlineT', '1', '2016-02-17 15:04:52', '2016-02-17 15:04:52', 'linecons-user', '123', '7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', NULL, NULL);
--增加角色菜單關係
-- INSERT INTO `SecurityMenuRoleR` (`relId`, `roleId`, `menuId`, `createDateTime`, `modifyDateTime`, `founderId`, `founderName`, `modifierId`, `modifierName`) VALUES ('e6ca7332-e0fc-4042-a9be-a79a4c7c12b0', '7d6007e5-9659-441d-be00-5ro484f8856d', '0f0aed45-7079-48ca-a912-3c7f86a1fb85', '2016-02-23 16:06:42', '2016-02-23 16:06:42', '7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', NULL, NULL);

--创建用户资源关系表
create table SecurityUserResourceR
(
   userResourceRId      varchar(36) not null comment '用户资源关系ID',
   userId               varchar(36) comment '用户ID',
   belongModel          varchar(20) comment '所属模块',
   resourceType         varchar(36) comment '资源类型',
   resourceId           varchar(36) comment '资源ID',
   resourceName         varchar(20) comment '资源名称',
   primary key (userResourceRId)
);

alter table SecurityUserResourceR comment '用户资源关系表';



