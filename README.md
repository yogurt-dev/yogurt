# 构想 

搭建一套遵循规范、技术先进、减少工作量的开发框架。

它就像Restfull一样，并不是什么新的框架，而是整合现有技术的最佳实践。

# 特点 

- **很规范**：严格遵循《阿里巴巴 Java 开发手册》，以及各种最佳实践，eg:restful命名最佳实践、请求结果使用HTTP状态位。
- **无侵入**：只是运用和扩展各种技术，不修改其源码。
- **很清爽**：入门毫无压力，因为我们并不想自成体系，只提供了必要的实现。
- **很高效**：再也不用写mybatis的xml了，在java文件和xml之前跳来跳去，字段对比来对比去的，一切都在java类中完成，想想都爽。
- **通用CRUD**：已经为您准备好，不用写代码。
- **代码生成器**：集成了JOOQ的代码生成器，Controller、Service、DAO以及属性对应的Enum一并生成。

# 涉及标准及技术

《阿里巴巴 Java 开发手册》

restful命名最佳实践

JOOQ（SpringBoot2开始将其纳入体系，开发效率优于mybatis）

lombok

guava

logback

# 表结构语句

ALTER TABLE `表名`
ADD COLUMN `creator_id`  bigint(9) NOT NULL DEFAULT 0 COMMENT '创建人ID',
ADD COLUMN `gmt_create`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER `creator_id`,
ADD COLUMN `modifier_id`  bigint(9) NULL COMMENT '修改人ID' AFTER `gmt_create`,
ADD COLUMN `gmt_modified`  datetime NULL COMMENT '修改时间' AFTER `modifier_id`,
ADD COLUMN `is_deleted`  tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识' AFTER `gmt_modified`;

# 版权 | License

[Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)
