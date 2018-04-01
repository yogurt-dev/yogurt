package com.github.jyoghurt.wechatbasic.enums;

/**
 * Created by zhangjl on 2015/11/18.
 */
/*菜单类型  菜单的响应动作类型合为一个枚举*/
public enum WeChatMenusTypeEnum {
    click,//点击事件
    view,//展示
    media_id,//素材
    view_limited,//图文消息(连接)
    location_select,//发送位置
    pic_weixin,//微信相册发图
    pic_photo_or_album,//拍照或相册发图
    miniprogram
}
