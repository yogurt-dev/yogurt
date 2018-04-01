package com.github.jyoghurt.wechatbasic.common.resp;

/**
 * Created by Lvyu on 2015/9/23.
 */
public class Filter {
   //用于设定是否向全部用户发送，值为true或false，选择true该消息群发给所有用户，选择false可根据group_id发送给指定群组的用户
    private boolean is_to_all;
     //组群id
    private String group_id;

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public boolean is_to_all() {
        return is_to_all;
    }

    public void setIs_to_all(boolean is_to_all) {
        this.is_to_all = is_to_all;
    }
}
