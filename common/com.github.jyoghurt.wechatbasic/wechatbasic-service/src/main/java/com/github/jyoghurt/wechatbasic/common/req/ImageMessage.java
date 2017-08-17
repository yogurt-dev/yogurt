package com.github.jyoghurt.wechatbasic.common.req;


/** 
 * 图片消息 
 *  
 * @author chihang 
 * @date 2015-08-30 
 */  
public class ImageMessage extends BaseMessage{
	  // 图片链接  
    private String PicUrl;  
  
    public String getPicUrl() {  
        return PicUrl;  
    }  
  
    public void setPicUrl(String picUrl) {  
        PicUrl = picUrl;  
    }  

}
