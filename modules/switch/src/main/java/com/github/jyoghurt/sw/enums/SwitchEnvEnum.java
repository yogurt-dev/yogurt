package com.github.jyoghurt.sw.enums;


public enum SwitchEnvEnum {

    DEVELOP_LOCALHOST(1),
    AUTOTEST(2),
    INTEGRATION_CONTINOUS(3),
    INTEGRATION_TEST(4),
    TRUNK_SUBFORMAL(5),
    TRUNK_RELEASE(6),
    TRAIN(7);

    private Integer code;

    SwitchEnvEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
