ALTER TABLE `SecurityMenuT`
ADD COLUMN `isGtask` TINYINT(1) NULL DEFAULT '1' AFTER `deleteFlag`;

ALTER TABLE `SecurityMenuT`
ADD COLUMN `gTaskUrl` VARCHAR(100) NULL AFTER `isGtask`;

ALTER TABLE `SecurityMenuT`
ADD COLUMN `gTaskType` VARCHAR(45) NULL COMMENT '待办任务的类型' AFTER `isGtask`;
