package com.github.jyoghurt.qqEmail.domain;

/**
 * Created by zhangjl on 2015/10/27.
 */
public class UserInfoVo {
    private String Action; /*1=DEL, 2=ADD, 3=MOD*/
    private String Alias;/*帐号名，帐号名为邮箱格式*/
    private String Name;/*姓名*/
    private String Gender;/*性别：1=男，2=女*/
    private String SlaveList;/*别名列表，用逗号分隔*/
    private String Position;/*职位*/
    private String Tel;/*联系电话*/
    private String Mobile;/*手机*/
    private String ExtId;/*编号*/
    private Party PartyList;/*部门列表*/
    private String OpenType;/*成员状态：1=启用，2=禁用*/

    public String getAction() {
        return Action;
    }

    public UserInfoVo setAction(String action) {
        Action = action;
        return this;
    }

    public String getAlias() {
        return Alias;
    }

    public UserInfoVo setAlias(String alias) {
        Alias = alias;
        return this;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getSlaveList() {
        return SlaveList;
    }

    public void setSlaveList(String slaveList) {
        SlaveList = slaveList;
    }

    public String getPosition() {
        return Position;
    }

    public void setPosition(String position) {
        Position = position;
    }

    public String getTel() {
        return Tel;
    }

    public void setTel(String tel) {
        Tel = tel;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getExtId() {
        return ExtId;
    }

    public void setExtId(String extId) {
        ExtId = extId;
    }



    public String getOpenType() {
        return OpenType;
    }

    public void setOpenType(String openType) {
        OpenType = openType;
    }

    public Party getPartyList() {
        return PartyList;
    }

    public void setPartyList(Party partyList) {
        PartyList = partyList;
    }
}
