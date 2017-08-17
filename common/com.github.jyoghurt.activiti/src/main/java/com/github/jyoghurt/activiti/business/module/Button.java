package com.github.jyoghurt.activiti.business.module;

/**
 * Created by DELL on 2017/5/18.
 */
public class Button {
    /**
     * 按钮Id
     */
    private String Id;
    /**
     * 按钮显示规则
     */
    private String btnRule;
    /**
     * 是否显示  true 是 false 否 默认false按钮
     */
    private boolean flag = false;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getBtnRule() {
        return btnRule;
    }

    public void setBtnRule(String btnRule) {
        this.btnRule = btnRule;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
