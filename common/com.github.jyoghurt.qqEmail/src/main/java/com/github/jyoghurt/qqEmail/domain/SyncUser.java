package com.github.jyoghurt.qqEmail.domain;

/**
 * Created by zhangjl on 2015/11/9.
 */
public class SyncUser {
    private String Action; /*1=DEL, 2=ADD, 3=MOD*/
    private String Alias;/*帐号名，帐号名为邮箱格式*/
    private String Name;/*姓名*/
    private String Gender;/*性别：1=男，2=女*/
    private String Slave;/*别名列表，用逗号分隔*/
    private String Position;/*职位*/
    private String Tel;/*联系电话*/
    private String Mobile;/*手机*/
    private String ExtId;/*编号*/
    private String Password;/*密码*/
    private String Md5;/*是否为Md5密码 0为明文  1为Md5密码*/
    private String PartyPath;/*部门列表*/
    private String OpenType;/*成员状态：1=启用，2=禁用*/

    public String getAction() {
        return Action;
    }

    public SyncUser setAction(String action) {
        Action = action;
        return this;
    }

    public String getName() {
        return Name;
    }

    public SyncUser setName(String name) {
        Name = name;
        return this;
    }

    public String getAlias() {
        return Alias;
    }

    public SyncUser setAlias(String alias) {
        Alias = alias;
        return this;
    }

    public String getGender() {
        return Gender;
    }

    public SyncUser setGender(String gender) {
        Gender = gender;
        return this;
    }

    public String getSlave() {
        return Slave;
    }

    public SyncUser setSlave(String slave) {
        Slave = slave;
        return this;
    }

    public String getPassword() {
        return Password;
    }

    public SyncUser setPassword(String password) {
        Password = password;
        return this;
    }

    public String getMd5() {
        return Md5;
    }

    public SyncUser setMd5(String md5) {
        Md5 = md5;
        return this;
    }

    public String getPosition() {
        return Position;
    }

    public SyncUser setPosition(String position) {
        Position = position;
        return this;
    }

    public String getTel() {
        return Tel;
    }

    public SyncUser setTel(String tel) {
        Tel = tel;
        return this;
    }

    public String getMobile() {
        return Mobile;
    }

    public SyncUser setMobile(String mobile) {
        Mobile = mobile;
        return this;
    }

    public String getExtId() {
        return ExtId;
    }

    public SyncUser setExtId(String extId) {
        ExtId = extId;
        return this;
    }

    public String getPartyPath() {
        return PartyPath;
    }

    public SyncUser setPartyPath(String partyPath) {
        PartyPath = partyPath;
        return this;
    }

    public String getOpenType() {
        return OpenType;
    }

    public SyncUser setOpenType(String openType) {
        OpenType = openType;
        return this;
    }
}
