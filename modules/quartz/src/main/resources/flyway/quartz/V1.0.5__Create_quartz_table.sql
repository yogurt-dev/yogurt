


UPDATE `ScheduleJob` SET `jobId`='2', `jobName`='Logistical', `jobGroup`='Logistical',
`jobState`='0', `cronExpression`='0 0 14  * * ? ', `description`='物流单', `className`='com.df.community.supports.manager.scheduled.SupportsLogisticalDistributeOrderTimerScheduled',
`beanName`='', `createDateTime`=NULL, `modifyDateTime`=NULL, `founderId`=NULL,
`founderName`=NULL, `modifierId`=NULL, `modifierName`=NULL, `deleteFlag`='0' WHERE (`jobId`='2');
commit;
