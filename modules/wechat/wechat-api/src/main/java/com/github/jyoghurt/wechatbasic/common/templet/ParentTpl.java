package com.github.jyoghurt.wechatbasic.common.templet;

import net.sf.json.JSONObject;

/**
 * user: dell
 * date: 2016/4/25.
 */
public class ParentTpl {
    /**
     * 接收人 即openId
     */
    private String touser;
    /**
     * 模板Id
     */
    private String template_id;
    /**
     * 表单id
     */
    private String form_id;
    /**
     * 点击后跳转的url
     */
    private String url;
    /**
     * 模板内容
     */
    private JSONObject data;

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public String getForm_id() {
        return form_id;
    }

    public void setForm_id(String form_id) {
        this.form_id = form_id;
    }

    @Override
    public String toString() {
        return "ParentTpl{" +
                "touser='" + touser + '\'' +
                ", template_id='" + template_id + '\'' +
                ", url='" + url + '\'' +
                ", data=" + data +
                '}';
    }
}
