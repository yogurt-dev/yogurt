package com.github.jyoghurt.sw.vo;

public class SwitchVo {

    /**
     * 开关组key
     */
    private String switchGroupKey;
    /**
     * 开关状态
     */
    private Boolean switchStatus;


    public String getSwitchGroupKey() {
        return switchGroupKey;
    }

    public void setSwitchGroupKey(String switchGroupKey) {
        this.switchGroupKey = switchGroupKey;
    }

    public Boolean getSwitchStatus() {
        return switchStatus;
    }

    public void setSwitchStatus(Boolean switchStatus) {
        this.switchStatus = switchStatus;
    }
}
