package com.github.jyoghurt.security.securityUserT.domain;

/**
 * @Project: 驴鱼社区-车险帮
 * @Package: com.df.security.securityUserT.domain
 * @Description:
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2016-02-03 15:48
 */
public class LoginUserInfoBean {

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    //访问的url
    private String uri;

    //创建的sessionId
    private String sessionId;

    private String localAddress;

    private String remoteAddress;

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }
}
