package com.github.jyoghurt.security.securityUserT.domain;

import java.math.BigInteger;

/**
 * @Project: 驴鱼社区-车险帮
 * @Package: com.df.security.securityUserT.domain
 * @Description:
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2016-01-25 14:28
 */
public class CertT {

    //名字与姓氏
    private String cn;
    //组织单位名称
    private String ou;
    //组织名称
    private String o;
    //城市或区域名称
    private String l;
    //州或省份名称
    private String st;
    //单位的两字母国家代码
    private String c;
    //别名
    private String alias;

    //序号
    private BigInteger serialNumber;

    //服务端密钥库存储路径
    private String path;

    //客户端密钥存储路径
    private String cpath;

    public String getClientPwd() {
        return clientPwd;
    }

    public void setClientPwd(String clientPwd) {
        this.clientPwd = clientPwd;
    }

    //密钥库密码
    private String password;

    //客户端证书密码
    private String clientPwd;

    public String getCpath() {
        return cpath;
    }

    public void setCpath(String cpath) {
        this.cpath = cpath;
    }





    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getOu() {
        return ou;
    }

    public void setOu(String ou) {
        this.ou = ou;
    }

    public String getO() {
        return o;
    }

    public void setO(String o) {
        this.o = o;
    }

    public String getL() {
        return l;
    }

    public void setL(String l) {
        this.l = l;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public BigInteger getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(BigInteger serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
