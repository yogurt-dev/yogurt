构想 
====

搭建一套遵循规范、技术先进、减少工作量的开发框架。  
它就像Restfull一样，并不是什么新的框架，而是整合现有技术的最佳实践。

特点 
====
- **很规范**：严格遵循《阿里巴巴 Java 开发手册》，以及各种最佳实践，eg:restful命名最佳实践、请求结果使用HTTP状态位。
- **无侵入**：只是运用和扩展各种技术，不修改其源码。
- **很清爽**：入门毫无压力，因为我们并不想自成体系，只提供了必要的实现。
- **很高效**：再也不用写mybatis的xml了，在java文件和xml之前跳来跳去，字段对比来对比去的，一切都在java类中完成，想想都爽。
- **通用CRUD**：已经为您准备好，不用写代码。
- **代码生成器**：集成了JOOQ的代码生成器，Controller、Service、DAO以及属性对应的Enum一并生成。

涉及标准及技术
=============
《阿里巴巴 Java 开发手册》  
[Restful命名规则](https://www.restapitutorial.com/lessons/restfulresourcenaming.html)   
[JOOQ](http://www.jooq.org)（SpringBoot2开始将其纳入体系，开发效率优于mybatis）  
Lombok  
Guava  
Logback  
Flyway  

表结构语句
=========
1.以下是每个表的基础字段，由yogurt负责维护，无需额外编码
```sql
ALTER TABLE `表名`  
ADD COLUMN `creator_id`  bigint NOT NULL DEFAULT 0 COMMENT '创建人ID',  
ADD COLUMN `gmt_create`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER `creator_id`,  
ADD COLUMN `modifier_id`  bigint NULL COMMENT '修改人ID' AFTER `gmt_create`,  
ADD COLUMN `gmt_modified`  datetime NULL COMMENT '修改时间' AFTER `modifier_id`,  
ADD COLUMN `is_deleted`  tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识' AFTER `gmt_modified`;  
```

2.枚举类型注释格式  
 渠道类型(ALI:某宝,JD:东哥)  

代码生成器
=========
直接使用JOOQ的配置即可，yogurt无需额外配置，具体示例：
[jooqConfig.xml](https://github.com/yogurt-dev/yogurt/blob/master/codegen/src/main/resources/jooqConfig.xml)

示例
=========
参见sample的[README.md](https://github.com/yogurt-dev/yogurt/blob/master/sample/README.md)  

技术支持
=========
QQ：442952891 
如果你觉得哪里可以更好，请联系我！  

版权 | License
==============
[Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)
