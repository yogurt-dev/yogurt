package com.github.jyoghurt.wechatbasic.domain;

import java.util.Date;

/**
 * Created by zhangjl on 2015/11/10.
 */
public class TokenEntity {
    private Date createTime;
    private String accessToken;

    public Date getCreateTime() {
        return createTime;
    }

    public TokenEntity setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public TokenEntity setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }
}
