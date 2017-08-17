SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `SecurityButtonT`
-- ----------------------------
CREATE TABLE `SecurityButtonT` (
`buttonId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '按钮名称' ,
`buttonName`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '按钮名称' ,
`menuId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '功能id' ,
`buttonCode`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '按钮编码' ,
`createDateTime`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`modifyDateTime`  datetime NULL DEFAULT NULL COMMENT '修改时间' ,
`operatorId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作id' ,
`operatorName`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作人' ,
PRIMARY KEY (`buttonId`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `SecurityDataDic`
-- ----------------------------
CREATE TABLE `SecurityDataDic` (
`id`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '字典ID' ,
`dicName`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字典名称' ,
`key`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'key' ,
`value`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'value' ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `SecurityMenuRoleR`
-- ----------------------------
CREATE TABLE `SecurityMenuRoleR` (
`relId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '关系ID' ,
`roleId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色ID' ,
`menuId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单ID' ,
`createDateTime`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`modifyDateTime`  datetime NULL DEFAULT NULL COMMENT '修改时间' ,
`operatorId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户ID' ,
`operatorName`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`relId`),
FOREIGN KEY (`menuId`) REFERENCES `SecurityMenuT` (`menuId`) ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (`roleId`) REFERENCES `SecurityRoleT` (`roleId`) ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (`operatorId`) REFERENCES `SecurityUserT` (`userId`) ON DELETE SET NULL ON UPDATE CASCADE,
INDEX `FK_Reference_5` (`roleId`) USING BTREE ,
INDEX `FK_Reference_9` (`operatorId`) USING BTREE ,
INDEX `FK_Reference_27` (`menuId`) USING BTREE
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='菜单角色关系表'

;

-- ----------------------------
-- Table structure for `SecurityMenuT`
-- ----------------------------
CREATE TABLE `SecurityMenuT` (
`menuId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单ID' ,
`parentId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '父菜单ID' ,
`menuName`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单名称' ,
`menuUrl`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单链接' ,
`isLeaf`  varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`createDateTime`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`modifyDateTime`  datetime NULL DEFAULT NULL COMMENT '修改时间' ,
`icon`  varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图标' ,
`sortId`  int(3) NULL DEFAULT NULL COMMENT '排序ID' ,
`operatorId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户ID' ,
`operatorName`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`menuId`),
FOREIGN KEY (`operatorId`) REFERENCES `SecurityUserT` (`userId`) ON DELETE SET NULL ON UPDATE CASCADE,
INDEX `FK_Reference_1` (`operatorId`) USING BTREE
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='系统菜单管理表'

;

-- ----------------------------
-- Table structure for `SecurityRoleButtonR`
-- ----------------------------
CREATE TABLE `SecurityRoleButtonR` (
`mrbId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键id' ,
`roleId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色id' ,
`buttonId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '按钮code' ,
`createDateTime`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`modifyDateTime`  datetime NULL DEFAULT NULL COMMENT '修改时间' ,
`operatorId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作人id' ,
`operatorName`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作人姓名' ,
PRIMARY KEY (`mrbId`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `SecurityRoleT`
-- ----------------------------
CREATE TABLE `SecurityRoleT` (
`roleId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色ID' ,
`roleName`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色名称' ,
`roleType`  varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色类型(0-系统角色、1-用户角色)' ,
`belongUnit`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属组织机构' ,
`createDateTime`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`modifyDateTime`  datetime NULL DEFAULT NULL COMMENT '修改时间' ,
`operatorId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作人' ,
`operatorName`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`roleId`),
FOREIGN KEY (`operatorId`) REFERENCES `SecurityUserT` (`userId`) ON DELETE SET NULL ON UPDATE CASCADE,
INDEX `FK_Reference_2` (`operatorId`) USING BTREE
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='系统角色表'

;

-- ----------------------------
-- Table structure for `SecuritySyslogT`
-- ----------------------------
CREATE TABLE `SecuritySyslogT` (
`logId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '日志ID' ,
`ipAddress`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'IP地址' ,
`moduleName`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模块名称' ,
`state`  int(1) NULL DEFAULT NULL COMMENT '状态' ,
`logMessage`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '日志内容' ,
`errorMessage`  varchar(10000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '异常信息' ,
`classMethodName`  varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类方法名' ,
`methodParameterValues`  longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '参数列表' ,
`systemType`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '系统类型' ,
`clientType`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '客户端类型' ,
`invokeDuration`  bigint(20) NULL DEFAULT NULL COMMENT '调用时长' ,
`createDateTime`  datetime NULL DEFAULT NULL COMMENT '操作时间' ,
`modifyDateTime`  datetime NULL DEFAULT NULL ,
`operatorId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作人ID' ,
`operatorName`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`logId`),
INDEX `FK_Reference_8` (`operatorId`) USING BTREE
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `SecurityUnitT`
-- ----------------------------
CREATE TABLE `SecurityUnitT` (
`unitId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '组织机构ID' ,
`parentId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '上级单位' ,
`unitName`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '组织机构名称' ,
`type`  varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '组织机构类型 0-4s店 1-保险代理公司 2-保险公估公司' ,
`compType`  varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '1' COMMENT '类型 0-公司 1-部门' ,
`appId`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订阅号appId' ,
`secretKey`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订阅号secretKey' ,
`appIdAccount`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订阅号账号' ,
`appIdPwd`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订阅号密码' ,
`appIdF`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '服务号appId' ,
`secretKeyF`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '服务号secretKeyF' ,
`appIdFAccount`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '服务号账号' ,
`appIdFPwd`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '服务号密码' ,
`appIdQ`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '企业号appIdQ' ,
`secretKeyQ`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '服务号secretKeyQ' ,
`appIdQAccount`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '企业号账号' ,
`appIdQPwd`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '企业号密码' ,
`clientId`  varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '腾讯企业邮箱账户' ,
`clientSecret`  varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '腾讯企业邮箱密钥' ,
`bsflag`  varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '是否删除 0-否，1-是' ,
`acol1ColAssessor`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公估属性1' ,
`acol2ColAssessor`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公估属性2' ,
`pcol1ColProxy`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '代理属性1' ,
`pcol2ColProxy`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '代理属性2' ,
`col1Col4s`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '业务所在地KEY' ,
`col2Col4s`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '业务所在地VALUE' ,
`createDateTime`  datetime NULL DEFAULT NULL ,
`modifyDateTime`  datetime NULL DEFAULT NULL ,
`operatorId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`operatorName`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
sortId int(3) DEFAULT NULL,
PRIMARY KEY (`unitId`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `SecurityUserRoleR`
-- ----------------------------
CREATE TABLE `SecurityUserRoleR` (
`relId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '关系ID' ,
`roleId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色ID' ,
`userId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户ID' ,
`createDateTime`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`modifyDateTime`  datetime NULL DEFAULT NULL COMMENT '修改时间' ,
`operatorId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作人' ,
`operatorName`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`relId`),
FOREIGN KEY (`roleId`) REFERENCES `SecurityRoleT` (`roleId`) ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (`userId`) REFERENCES `SecurityUserT` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE,
INDEX `FK_Reference_10` (`roleId`) USING BTREE ,
INDEX `FK_Reference_11` (`userId`) USING BTREE
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='用户角色关系表'

;

-- ----------------------------
-- Table structure for `SecurityUserT`
-- ----------------------------
CREATE TABLE `SecurityUserT` (
`userId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户ID' ,
`extId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '编号' ,
`userName`  varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名称' ,
`slaveList`  varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '别名列表，用逗号分隔' ,
`mobile`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号码' ,
`linkWay`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联系方式' ,
`emailAddr`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱地址' ,
`userAccount`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '账号' ,
`position`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`passwd`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`belongOrg`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属单位' ,
`belongOrgName`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属单位名称' ,
`type`  varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '1' COMMENT '用户类型（0-系统类型、1-用户类型）' ,
`createDateTime`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`modifyDateTime`  datetime NULL DEFAULT NULL COMMENT '修改时间' ,
`openType`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '成员状态：1=启用，2=禁用' ,
`operatorId`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作人' ,
`operatorName`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`userId`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='用户表'

;



-- insert start
-- 用户
-- INSERT INTO SecurityUserT (userId, extId, userName, slaveList, mobile, linkWay, emailAddr, userAccount, position, passwd, belongOrg, belongOrgName, type, createDateTime, modifyDateTime, openType, operatorId, operatorName) VALUES ('7d6007e5-9659-441d-be00-5us484f8856d', null, '系统超级管理员', null, null, '15998819817', 'admin@neusoft.com', 'admin', null, '202cb962ac59075b964b07152d234b70', '-1', null, '0', '2015-09-30 10:26:11', '2015-10-20 16:57:01', null, '7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员');
-- 角色
-- INSERT INTO SecurityRoleT (roleId, roleName, roleType, belongUnit, createDateTime, modifyDateTime, operatorId, operatorName) VALUES ('7d6007e5-9659-441d-be00-5ro484f8856d', '系统超级管理员', '0', '', '2015-09-23 10:29:39', '2015-09-23 10:29:43', '7d6007e5-9659-441d-be00-5us484f8856d', 'admin');


-- 菜单
-- INSERT INTO SecurityMenuT (menuId, parentId, menuName, menuUrl, isLeaf, createDateTime, modifyDateTime, icon, sortId,operatorId, operatorName) VALUES ('-1', '9999', '菜单', null, '0', '2015-10-24 10:09:01', '2015-10-24 10:09:01', '', 0,'7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员');
-- INSERT INTO SecurityMenuT (menuId, parentId, menuName, menuUrl, isLeaf, createDateTime, modifyDateTime, icon, sortId, operatorId, operatorName) VALUES ('0013db53-59da-46f9-9766-7d411b0baa2b', '11', '组织管理', '/sysManage/securityUnitT/guidUManage', '1', '2015-10-24 10:09:01', '2015-10-24 10:09:01', 'linecons-tag', 1, '7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员');
-- INSERT INTO SecurityMenuT (menuId, parentId, menuName, menuUrl, isLeaf, createDateTime, modifyDateTime, icon, sortId, operatorId, operatorName) VALUES ('11', '-1', '系统管理', '', '0', '2015-08-21 14:55:58', '2015-08-21 14:56:00', 'linecons-cog', 1, null, 'a');
-- INSERT INTO SecurityMenuT (menuId, parentId, menuName, menuUrl, isLeaf, createDateTime, modifyDateTime, icon, sortId, operatorId, operatorName) VALUES ('110', '11', '角色管理', '/sysManage/roleManage/SecurityRoleT', '1', '2015-08-21 14:55:58', '2015-08-21 14:55:58', 'linecons-graduation-cap', 4, null, 'a');
-- INSERT INTO SecurityMenuT (menuId, parentId, menuName, menuUrl, isLeaf, createDateTime, modifyDateTime, icon, sortId, operatorId, operatorName) VALUES ('17', '11', '功能管理', '/sysManage/menuManage/SecurityMenuT', '1', '2015-09-09 13:53:26', '2015-09-09 13:53:30', 'linecons-key', 3, null, 'a');
-- INSERT INTO SecurityMenuT (menuId, parentId, menuName, menuUrl, isLeaf, createDateTime, modifyDateTime, icon, sortId, operatorId, operatorName) VALUES ('18', '11', '操作日志', '/sysManage/SecuritySyslogT', '1', '2015-09-09 13:54:31', '2015-09-09 13:54:35', 'linecons-pencil', 17, null, 'a');

-- 角色菜单
-- INSERT INTO SecurityMenuRoleR (relId, roleId, menuId, createDateTime, modifyDateTime, operatorId, operatorName) VALUES ('f552aa0d-9603-47ec-8658-7b54c2f31fa9', '7d6007e5-9659-441d-be00-5ro484f8856d', '0013db53-59da-46f9-9766-7d411b0baa2b', '2015-10-30 13:56:39', '2015-10-30 13:56:39', '7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员');
-- INSERT INTO SecurityMenuRoleR (relId, roleId, menuId, createDateTime, modifyDateTime, operatorId, operatorName) VALUES ('33ff9f77-6851-4305-a695-99bfae3f5655', '7d6007e5-9659-441d-be00-5ro484f8856d', '18', '2015-10-30 13:56:40', '2015-10-30 13:56:40', '7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员');
-- INSERT INTO SecurityMenuRoleR (relId, roleId, menuId, createDateTime, modifyDateTime, operatorId, operatorName) VALUES ('3dac7af9-8bcf-4c31-82a2-9f6abf893fca', '7d6007e5-9659-441d-be00-5ro484f8856d', '17', '2015-10-30 13:56:40', '2015-10-30 13:56:40', '7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员');
-- INSERT INTO SecurityMenuRoleR (relId, roleId, menuId, createDateTime, modifyDateTime, operatorId, operatorName) VALUES ('c4059bf5-3e3b-42cb-912d-f32589ed0590', '7d6007e5-9659-441d-be00-5ro484f8856d', '110', '2015-10-30 13:56:40', '2015-10-30 13:56:40', '7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员');
-- INSERT INTO SecurityMenuRoleR (relId, roleId, menuId, createDateTime, modifyDateTime, operatorId, operatorName) VALUES ('c9e87e5b-e560-4488-8019-5f9099a07001', '7d6007e5-9659-441d-be00-5ro484f8856d', '11', '2015-10-30 13:56:39', '2015-10-30 13:56:39', '7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员');

-- 组织机构
-- INSERT INTO SecurityUnitT (unitId, parentId, unitName, type, compType, appId, secretKey, appIdAccount, appIdPwd, appIdF, secretKeyF, appIdFAccount, appIdFPwd, appIdQ, secretKeyQ, appIdQAccount, appIdQPwd, bsflag, acol1ColAssessor, acol2ColAssessor, pcol1ColProxy, pcol2ColProxy, col1Col4s, col2Col4s, createDateTime, modifyDateTime, operatorId, operatorName) VALUES ('-1', '9999', '企业通讯录', null, null, '', '', 'ssss', '123', '', '', '', '', '', '', '', '', '0', '', '', '', '', '', '', null, '2015-10-29 14:29:24', '7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员');

-- 用户角色
-- INSERT INTO SecurityUserRoleR (relId, roleId, userId, createDateTime, modifyDateTime, operatorId, operatorName) VALUES ('344e339e-7e87-4724-a889-6c48249ea51f', '7d6007e5-9659-441d-be00-5ro484f8856d', '7d6007e5-9659-441d-be00-5us484f8856d', '2015-10-28 15:38:01', '2015-10-28 15:38:01', '7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员');





-- insert end
-- function


DROP FUNCTION IF EXISTS getChildCompanyList;
CREATE FUNCTION getChildCompanyList(rootId VARCHAR(1000)) RETURNS mediumtext CHARSET utf8
BEGIN
	DECLARE
		pTemp text (100000);

DECLARE
	cTemp text (100000);


SET pTemp = '$';


SET cTemp = rootId;


WHILE cTemp IS NOT NULL DO

SET pTemp = CONCAT(pTemp, ',', cTemp);

SELECT
	GROUP_CONCAT(unitId) INTO cTemp
FROM
	SecurityUnitT
WHERE
	FIND_IN_SET(parentId, cTemp) > 0;


END
WHILE;

RETURN pTemp;


END;


DROP FUNCTION IF EXISTS getChildCompanyPersonList;
CREATE FUNCTION getChildCompanyPersonList(rootId VARCHAR(50)) RETURNS longtext CHARSET utf8
BEGIN
	DECLARE
		pTemp longtext ;

DECLARE
	cTemp longtext ;


SET pTemp = '$';


SET cTemp = rootId;


WHILE cTemp IS NOT NULL DO

SET pTemp = concat(pTemp, ',', cTemp);

SELECT
	group_concat(uu.unitId) INTO cTemp
FROM
	(
		SELECT
			unit.unitId,
			unit.unitName,
			unit.parentId,
			unit.compType
		FROM
			SecurityUnitT unit
		WHERE
			unit.bsflag = '0'
		UNION
			SELECT
				u.userId AS 'unitId',
				u.userName AS 'unitName',
				u.belongOrg AS 'parentId',
				'3' AS 'compType'
			FROM
				SecurityUserT u
	) uu
WHERE
	FIND_IN_SET(uu.parentId, cTemp) > 0;


END
WHILE;

RETURN pTemp;


END;


DROP FUNCTION IF EXISTS getFatherNode;
CREATE FUNCTION getFatherNode(rootId VARCHAR(50)) RETURNS mediumtext CHARSET utf8
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
     END;