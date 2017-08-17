package pub.utils;

/**
 * @Project: 驴鱼社区-车险帮
 * @Package: com.df.motorInsurance.pub.utils
 * @Description: 枚举类，描述静态常量
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2015-09-02 15:00
 */
public enum SysVarEnum {
    USER_TYPE_MANAGER("0", "系统级"),
    USER_TYPE_USER("1", "用户级"),
    COMPANY_COMPTYPE("0", "公司"),
    DEPARTMENT_COMPTYPE("1", "部门"),
    HUM_COMPTYPE("3", "人员"),
    ROOT_COMPTYPE("2", "根公司"),
    ADMIN_ROLETYPE("0", "超级管理员角色"),
    SYSTEM_ROLETYPE("1", "系统角色"),
    USER_ROLETYPE("2", "用户角色"),
    YES_STATICVAR("0", "是"),
    NO_STATICVAR("1", "否"),
    CLIENT_ID("mayidongli", "客户端账号"),
    CLIENT_SECURITY
            ("a0a0efd54ed07c16842eac1c274d64ac", "客户端密钥"),
    NORMAL_MENUTYPE("0", "url"),
    UNNORMAL_MENUTYPE("1", "warn"),
    ADD_OPPERTYPE("2", "新增"),
    DELETE_OPPERTYPE("1", "删除"),
    MODIFY_OPPERTYPE("3", "修改"),
    TYPE_THIRDPART("3","第三方"),
    /*
    您的名字与姓氏是什么（CN）？
            [Unknown]：  StoneXing
    您的组织单位名称是什么（OU）？
            [Unknown]：  iFLYTEK
    您的组织名称是什么（O）？
            [Unknown]：  iFLYTEK
    您所在的城市或区域名称是什么（L）？
            [Unknown]：  合肥市
    您所在的州或省份名称是什么（ST）？
            [Unknown]：  安徽省
    该单位的两字母国家代码是什么（C）
    [Unknown]：  CN
    //    SECURITY_CA_ROOT_ISSUER("C=CN,ST=LN,L=SY,O=LVKJ,OU=LY,CN=LVKJ",
    //            "CA_ROOT_ISSUER"),
    //    SECURITY_CA_DEFAULT_SUBJECT("C=CN,ST=LN,L=SY,O=LVKJ,OU=LY,CN=",
    //            "CA_DEFAULT_SUBJECT"),
    //    SECURITY_CA_SHA("SHA256WithRSAEncryption", "CA_SHA"),
    */
    SECURITY_CA_C("CN", "CA_C"),
    SECURITY_CA_ST("辽宁省", "CA_ST"),
    SECURITY_CA_L("盘锦市", "CA_L"),
    SECURITY_CA_O("LYKJ", "CA_O"),
    SECURITY_CA_OU("LYKJ", "CA_OU"),
    SECURITY_CA_CN("StoneXing","CA_CN"),
    SECURITY_CA_STOREPASS("lvyuwoaini", "密钥库的密码");

    private String code;
    private String message;

    SysVarEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
