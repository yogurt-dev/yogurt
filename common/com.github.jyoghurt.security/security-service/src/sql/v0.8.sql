-- table start
CREATE TABLE SecurityButtonT
(
    buttonId VARCHAR(36) PRIMARY KEY NOT NULL,
    buttonName VARCHAR(20),
    menuId VARCHAR(36),
    buttonCode VARCHAR(20),
    createDateTime DATETIME,
    modifyDateTime DATETIME,
    operatorId VARCHAR(36),
    operatorName VARCHAR(20)
);
CREATE TABLE SecurityDataDic
(
    id VARCHAR(36) PRIMARY KEY NOT NULL,
    dicName VARCHAR(20),
    key VARCHAR(20),
    value VARCHAR(20)
);
CREATE TABLE SecurityInsuranceProxyCompaniesT
(
    companyId VARCHAR(36) PRIMARY KEY NOT NULL,
    relateTel VARCHAR(30),
    lncx VARCHAR(20),
    serviceAccount VARCHAR(20),
    comment VARCHAR(255),
    createDateTime DATETIME,
    modifyDateTime DATETIME,
    opperMan VARCHAR(32),
    operatorId VARCHAR(36),
    operatorName VARCHAR(20),
    companyName LONGTEXT
);
CREATE TABLE SecurityMenuRoleR
(
    relId VARCHAR(36) PRIMARY KEY NOT NULL,
    roleId VARCHAR(36),
    menuId VARCHAR(36),
    createDateTime DATETIME,
    modifyDateTime DATETIME,
    operatorId VARCHAR(36),
    operatorName VARCHAR(20)
);
CREATE TABLE SecurityMenuT
(
    menuId VARCHAR(36) PRIMARY KEY NOT NULL,
    parentId VARCHAR(36),
    menuName VARCHAR(20),
    menuUrl VARCHAR(100),
    isLeaf VARCHAR(1),
    createDateTime DATETIME,
    modifyDateTime DATETIME,
    icon VARCHAR(200),
    sortId INT,
    operatorId VARCHAR(36),
    operatorName VARCHAR(20)
);
CREATE TABLE SecurityRoleButtonR
(
    mrbId VARCHAR(36) PRIMARY KEY NOT NULL,
    roleId VARCHAR(36),
    buttonId VARCHAR(36),
    createDateTime DATETIME,
    modifyDateTime DATETIME,
    operatorId VARCHAR(36),
    operatorName VARCHAR(20)
);
CREATE TABLE SecurityRoleT
(
    roleId VARCHAR(36) PRIMARY KEY NOT NULL,
    roleName VARCHAR(20),
    roleType VARCHAR(2),
    belongUnit VARCHAR(36),
    createDateTime DATETIME,
    modifyDateTime DATETIME,
    operatorId VARCHAR(36),
    operatorName VARCHAR(20)
);
CREATE TABLE SecuritySyslogT
(
    logId VARCHAR(36) PRIMARY KEY NOT NULL,
    ipAddress VARCHAR(128),
    moduleName VARCHAR(20),
    state INT,
    logMessage VARCHAR(100),
    errorMessage VARCHAR(10000),
    classMethodName VARCHAR(500),
    methodParameterValues longtext,
    systemType VARCHAR(20),
    clientType VARCHAR(20),
    invokeDuration BIGINT,
    createDateTime DATETIME,
    modifyDateTime DATETIME,
    operatorId VARCHAR(36),
    operatorName VARCHAR(20)
);

CREATE TABLE SecurityUnitT (
  unitId varchar(36) NOT NULL COMMENT '组织机构ID',
  parentId varchar(36) NOT NULL COMMENT '上级单位',
  unitName varchar(20) DEFAULT NULL COMMENT '组织机构名称',
  type varchar(1) DEFAULT NULL COMMENT '组织机构类型 0-4s店 1-保险代理公司 2-保险公估公司',
  compType varchar(1) DEFAULT '1' COMMENT '类型 0-公司 1-部门',
  appId varchar(50) DEFAULT NULL COMMENT '订阅号appId',
  secretKey varchar(50) DEFAULT NULL COMMENT '订阅号secretKey',
  appIdAccount varchar(50) DEFAULT NULL COMMENT '订阅号账号',
  appIdPwd varchar(50) DEFAULT NULL COMMENT '订阅号密码',
  appIdF varchar(50) DEFAULT NULL COMMENT '服务号appId',
  secretKeyF varchar(50) DEFAULT NULL COMMENT '服务号secretKeyF',
  appIdFAccount varchar(50) DEFAULT NULL COMMENT '服务号账号',
  appIdFPwd varchar(50) DEFAULT NULL COMMENT '服务号密码',
  appIdQ varchar(50) DEFAULT NULL COMMENT '企业号appIdQ',
  secretKeyQ varchar(50) DEFAULT NULL COMMENT '服务号secretKeyQ',
  appIdQAccount varchar(50) DEFAULT NULL COMMENT '企业号账号',
  appIdQPwd varchar(50) DEFAULT NULL COMMENT '企业号密码',
  clientId varchar(30) DEFAULT NULL COMMENT '腾讯企业邮箱账户',
  clientSecret varchar(40) DEFAULT NULL COMMENT '腾讯企业邮箱密钥',
  bsflag varchar(1) DEFAULT '0' COMMENT '是否删除 0-否，1-是',
  acol1ColAssessor varchar(20) DEFAULT NULL COMMENT '公估属性1',
  acol2ColAssessor varchar(20) DEFAULT NULL COMMENT '公估属性2',
  pcol1ColProxy varchar(20) DEFAULT NULL COMMENT '代理属性1',
  pcol2ColProxy varchar(20) DEFAULT NULL COMMENT '代理属性2',
  col1Col4s varchar(20) DEFAULT NULL COMMENT '业务所在地KEY',
  col2Col4s varchar(20) DEFAULT NULL COMMENT '业务所在地VALUE',
  createDateTime datetime DEFAULT NULL,
  modifyDateTime datetime DEFAULT NULL,
  operatorId varchar(36) DEFAULT NULL,
  operatorName varchar(20) DEFAULT NULL,
  PRIMARY KEY (unitId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE SecurityUserRoleR
(
    relId VARCHAR(36) PRIMARY KEY NOT NULL,
    roleId VARCHAR(36),
    userId VARCHAR(36),
    createDateTime DATETIME,
    modifyDateTime DATETIME,
    operatorId VARCHAR(36),
    operatorName VARCHAR(20)
);
CREATE TABLE SecurityUserT
(
    userId VARCHAR(36) PRIMARY KEY NOT NULL,
    extId VARCHAR(36),
    userName VARCHAR(30),
    slaveList VARCHAR(200),
    mobile VARCHAR(20),
    linkWay VARCHAR(20),
    emailAddr VARCHAR(50),
    userAccount VARCHAR(20),
    position VARCHAR(20),
    passwd VARCHAR(100),
    belongOrg VARCHAR(36),
    belongOrgName VARCHAR(20),
    type VARCHAR(2) DEFAULT '1',
    createDateTime DATETIME,
    modifyDateTime DATETIME,
    openType CHAR(1),
    operatorId VARCHAR(36),
    operatorName VARCHAR(20)
);
ALTER TABLE SecurityMenuRoleR ADD FOREIGN KEY (menuId) REFERENCES SecurityMenuT (menuId) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE SecurityMenuRoleR ADD FOREIGN KEY (roleId) REFERENCES SecurityRoleT (roleId) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE SecurityMenuRoleR ADD FOREIGN KEY (operatorId) REFERENCES SecurityUserT (userId) ON DELETE SET NULL ON UPDATE CASCADE;
CREATE INDEX FK_Reference_27 ON SecurityMenuRoleR (menuId);
CREATE INDEX FK_Reference_5 ON SecurityMenuRoleR (roleId);
CREATE INDEX FK_Reference_9 ON SecurityMenuRoleR (operatorId);
ALTER TABLE SecurityMenuT ADD FOREIGN KEY (operatorId) REFERENCES SecurityUserT (userId) ON DELETE SET NULL ON UPDATE CASCADE;
CREATE INDEX FK_Reference_1 ON SecurityMenuT (operatorId);
ALTER TABLE SecurityRoleT ADD FOREIGN KEY (operatorId) REFERENCES SecurityUserT (userId) ON DELETE SET NULL ON UPDATE CASCADE;
CREATE INDEX FK_Reference_2 ON SecurityRoleT (operatorId);
CREATE INDEX FK_Reference_8 ON SecuritySyslogT (operatorId);
ALTER TABLE SecurityUserRoleR ADD FOREIGN KEY (roleId) REFERENCES SecurityRoleT (roleId) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE SecurityUserRoleR ADD FOREIGN KEY (userId) REFERENCES SecurityUserT (userId) ON DELETE CASCADE ON UPDATE CASCADE;
CREATE INDEX FK_Reference_10 ON SecurityUserRoleR (roleId);
CREATE INDEX FK_Reference_11 ON SecurityUserRoleR (userId);

-- table end

-- insert start
-- 用户
-- INSERT INTO SecurityUserT (userId, extId, userName, slaveList, mobile, linkWay, emailAddr, userAccount, position, passwd, belongOrg, belongOrgName, type, createDateTime, modifyDateTime, openType, operatorId, operatorName) VALUES ('7d6007e5-9659-441d-be00-5us484f8856d', null, '系统超级管理员', null, null, '15998819817', 'admin@neusoft.com', 'admin', null, '202cb962ac59075b964b07152d234b70', '-1', null, '0', '2015-09-30 10:26:11', '2015-10-20 16:57:01', null, '7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员');
-- 角色
-- INSERT INTO SecurityRoleT (roleId, roleName, roleType, belongUnit, createDateTime, modifyDateTime, operatorId, operatorName) VALUES ('7d6007e5-9659-441d-be00-5ro484f8856d', '系统超级管理员', '0', '', '2015-09-23 10:29:39', '2015-09-23 10:29:43', '7d6007e5-9659-441d-be00-5us484f8856d', 'admin');


-- 菜单
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
-- CREATE  FUNCTION getChildCompanyList (rootId VARCHAR(1000)) RETURNS varchar(1000) CHARSET utf8
-- BEGIN
-- 	DECLARE
-- 		pTemp VARCHAR (1000);
--
-- DECLARE
-- 	cTemp VARCHAR (1000);
--
--
-- SET pTemp = '$';
--
--
-- SET cTemp = rootId;
--
--
-- WHILE cTemp IS NOT NULL DO
--
-- SET pTemp = CONCAT(pTemp, ',', cTemp);
--
-- SELECT
-- 	GROUP_CONCAT(unitId) INTO cTemp
-- FROM
-- 	SecurityUnitT
-- WHERE
-- 	FIND_IN_SET(parentId, cTemp) > 0;
--
--
-- END
-- WHILE;
--
-- RETURN pTemp;
--
--
-- END;
--
-- CREATE  FUNCTION getChildCompanyPersonList (rootId VARCHAR(50)) RETURNS varchar(1000) CHARSET utf8
-- BEGIN
-- 	DECLARE
-- 		pTemp VARCHAR (1000);
--
-- DECLARE
-- 	cTemp VARCHAR (1000);
--
--
-- SET pTemp = '$';
--
--
-- SET cTemp = rootId;
--
--
-- WHILE cTemp IS NOT NULL DO
--
-- SET pTemp = concat(pTemp, ',', cTemp);
--
-- SELECT
-- 	group_concat(uu.unitId) INTO cTemp
-- FROM
-- 	(
-- 		SELECT
-- 			unit.unitId,
-- 			unit.unitName,
-- 			unit.parentId,
-- 			unit.compType
-- 		FROM
-- 			SecurityUnitT unit
-- 		WHERE
-- 			unit.bsflag = '0'
-- 		UNION
-- 			SELECT
-- 				u.userId AS 'unitId',
-- 				u.userName AS 'unitName',
-- 				u.belongOrg AS 'parentId',
-- 				'3' AS 'compType'
-- 			FROM
-- 				SecurityUserT u
-- 	) uu
-- WHERE
-- 	FIND_IN_SET(uu.parentId, cTemp) > 0;
--
--
-- END
-- WHILE;
--
-- RETURN pTemp;
--
--
-- END;
--
-- CREATE  FUNCTION getFatherNode(rootId VARCHAR(50)) RETURNS varchar(1000) CHARSET utf8
-- BEGIN
--        DECLARE pTemp VARCHAR(1000);
--        DECLARE cTemp VARCHAR(1000);
--
--        SET pTemp = '$';
--        SET cTemp =cast(rootId as CHAR);
--
--        WHILE cTemp is not null DO
--          SET pTemp = concat(pTemp,',',cTemp);
--          SELECT group_concat(parentId) INTO cTemp FROM SecurityUnitT
--          WHERE FIND_IN_SET(unitId,cTemp)>0;
--        END WHILE;
--        RETURN pTemp;
--      END;


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
CREATE FUNCTION getChildCompanyPersonList(rootId VARCHAR(50)) RETURNS mediumtext CHARSET utf8
BEGIN
	DECLARE
		pTemp text (100000);

DECLARE
	cTemp text (100000);


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


