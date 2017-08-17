DELETE from ScheduleJob  where jobId='7';

 ALTER TABLE `ScheduleJob`
MODIFY COLUMN `jobGroup`  varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '任务分组' AFTER `jobName`;

INSERT INTO `ScheduleJob` (`jobId`, `jobName`, `jobGroup`, `jobState`, `cronExpression`, `description`, `className`, `beanName`,
`createDateTime`, `modifyDateTime`, `founderId`, `founderName`, `modifierId`, `modifierName`, `deleteFlag`)
 VALUES ('7', 'SyncOrderPaymentStateSchedule', 'SyncOrderPaymentStateSchedule', '0', '0 0/1 * * * ?', '定期同步支付状态',
 'com.df.community.member.order.scheduled.SyncOrderPaymentStateSchedule', '', NULL, NULL, NULL, NULL, NULL, NULL, '0');
commit;