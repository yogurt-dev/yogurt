package com.github.jyoghurt.qqEmail.domain;

import java.util.List;

/**
 * Created by zhangjl on 2015/10/27.
 */
public class UserUpdateListVo {
    private String Ver;
    private String Count;
    private List<UserInfoVo> List;

    public String getVer() {
        return Ver;
    }

    public void setVer(String ver) {
        Ver = ver;
    }

    public String getCount() {
        return Count;
    }

    public void setCount(String count) {
        Count = count;
    }

    public List getList() {
        return List;
    }

    public void setList(List list) {
        List = list;
    }
}
