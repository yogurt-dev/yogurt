package com.github.jyoghurt.security.securityUnitT.enums;

/**
 * @Project: 驴鱼社区
 * @Package: com.df.security.securityUnitT.enums
 * @Description: 公司类型枚举
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2017-03-20 15:24
 */
public enum UnitTypeEnum {
    COMPANY("0","公司"),
    DEPARTMENT("1","部门");
    private String typeCode;
    private String typeName;

    UnitTypeEnum(String typeCode, String typeName) {
        this.typeCode = typeCode;
        this.typeName = typeName;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
