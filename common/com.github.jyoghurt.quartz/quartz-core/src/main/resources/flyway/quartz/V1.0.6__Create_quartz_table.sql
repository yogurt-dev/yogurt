


ALTER TABLE `ScheduleJob`
ADD COLUMN `rollBackTheWrong`  tinyint(1) NOT NULL DEFAULT 0 AFTER `deleteFlag`;

commit;
