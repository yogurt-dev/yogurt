操作步骤：

1.修改application.yml中的数据库信息  

2.启动服务DemoApplication (用于执行flyway，初始化数据库)

3.运行maven插件codegen:generate

4.TestController中的方法都是注掉的，打开它，重启服务。