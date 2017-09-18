package com.github.jyoghurt.serial.domain;

import com.github.jyoghurt.core.domain.BaseEntity;


@javax.persistence.Table(name = "SerialInfoT")
public class SerialInfoT extends BaseEntity<SerialInfoT> {

    /**
     * 业务主键ID
     */
    @javax.persistence.Id
    private String serialId;
    /**
     * 模块
     */
    private String module;
    /**
     * 生成时间
     */
    private java.util.Date generateDate;
    /**
     * 业务流水号
     */
    private Integer serialNo;

    public String getType() {
        return type;
    }

    public SerialInfoT setType(String type) {
        this.type = type;
        return this;
    }

    /**
     * 流水类型
     */
    private String type;

    public String getSerialId() {
        return this.serialId;
    }

    public SerialInfoT setSerialId(String serialId) {
        this.serialId = serialId;
        return this;
    }

    public String getModule() {
        return this.module;
    }

    public SerialInfoT setModule(String module) {
        this.module = module;
        return this;
    }

    public java.util.Date getGenerateDate() {
        return this.generateDate;
    }

    public SerialInfoT setGenerateDate(java.util.Date generateDate) {
        this.generateDate = generateDate;
        return this;
    }

    public Integer getSerialNo() {
        return this.serialNo;
    }

    public SerialInfoT setSerialNo(Integer serialNo) {
        this.serialNo = serialNo;
        return this;
    }
}
