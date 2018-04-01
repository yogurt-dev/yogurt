ALTER TABLE `DataDictItem` ADD COLUMN `parentDictItemCode`  varchar(36) NULL COMMENT '父数据字典值' AFTER `dicItemDesc`;

create TABLE DDI as select * from DataDictItem;

update DataDictItem set parentDictItemCode=(select ddi.dictItemCode from DDI ddi  where DataDictItem.parentDictItemId = ddi.dictItemId);

drop table DDI;

ALTER TABLE `DataDictValue` CHANGE COLUMN `bsflag` `deleteFlag`  tinyint(1) NULL DEFAULT 0 COMMENT '是否删除 0-否 1-是' ;

ALTER TABLE `DataDictItem` CHANGE COLUMN `bsflag` `deleteFlag`  tinyint(1) NULL DEFAULT 0 COMMENT '是否删除 0-否 1-是' ;