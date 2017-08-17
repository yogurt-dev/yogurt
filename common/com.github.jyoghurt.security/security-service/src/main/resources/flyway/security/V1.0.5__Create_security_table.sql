ALTER TABLE `SecurityUserT`
ADD COLUMN `loginVerification`  varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '用户登录校验码' AFTER `type`;

