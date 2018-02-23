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
    private String page;
    /**
     * 模板内容
     */
    private JSONObject data;
    /**
     * 模板需要放大的关键词
     */
    private String emphasis_keyword;

    public String getTouser() {
        return touser;
    }

    public ParentTpl setTouser(String touser) {
        this.touser = touser;
        return this;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public ParentTpl setTemplate_id(String template_id) {
        this.template_id = template_id;
        return this;
    }

    public String getPage() {
        return page;
    }

    public ParentTpl setPage(String page) {
        this.page = page;
        return this;
    }

    public JSONObject getData() {
        return data;
    }

    public ParentTpl setData(JSONObject data) {
        this.data = data;
        return this;
    }

    public String getForm_id() {
        return form_id;
    }

    public ParentTpl setForm_id(String form_id) {
        this.form_id = form_id;
        return this;
    }

    public String getEmphasis_keyword() {
        return emphasis_keyword;
    }

    public ParentTpl setEmphasis_keyword(String emphasis_keyword) {
        this.emphasis_keyword = emphasis_keyword;
        return  this;
    }

    @Override
    public String toString() {
        return "ParentTpl{" +
                "touser='" + touser + '\'' +
                ", template_id='" + template_id + '\'' +
                ", url='" + page + '\'' +
                ", data=" + data +
                '}';
    }
}
