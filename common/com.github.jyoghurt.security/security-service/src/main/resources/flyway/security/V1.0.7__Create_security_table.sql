ALTER TABLE `SecurityUserT`
ADD COLUMN `open_id`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '微信用户唯一标识' AFTER `loginVerification`;
