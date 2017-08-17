ALTER TABLE `SecurityMenuT`
MODIFY COLUMN `menuUrl`  varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL AFTER `menuName`;