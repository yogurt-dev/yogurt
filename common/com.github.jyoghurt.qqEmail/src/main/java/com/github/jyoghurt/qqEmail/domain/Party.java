package com.github.jyoghurt.qqEmail.domain;

import java.util.List;

/**
 * Created by zhangjl on 2015/10/27.
 */
public class Party {
    private String Count;/*部门数量*/
    private List<Unit> List;/*部门列表*/

    public String getCount() {
        return Count;
    }

    public void setCount(String count) {
        Count = count;
    }

    public List<Unit> getList() {
        return List;
    }

    public void setList(List<Unit> list) {
        List = list;
    }
}
