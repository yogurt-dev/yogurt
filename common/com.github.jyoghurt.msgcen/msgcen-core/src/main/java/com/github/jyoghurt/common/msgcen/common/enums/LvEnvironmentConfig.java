package com.github.jyoghurt.common.msgcen.common.enums;

/**
 * user:dell
 * date: 2016/7/18.
 */
public enum LvEnvironmentConfig {
    develop_continous,//开发持续集成环境
    develop_localhost,//开发本地环境
    integration_continous,//集成持续集成环境
    integration_test,//集成测试环境
    trunk_continous,//主干持续集成环境
    autotest,//自动化测试环境
    trunk_release,//主干正式环境
    trunk_subformal//主干准正式环境
}
