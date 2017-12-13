package com.github.jyoghurt.wechatbasic.common.pojo;

import java.util.List;

/**
 * 复杂按钮（父按钮） 
 *  
 * @author liufeng 
 * @date 2013-08-08 
 */  
public class ComplexButton extends Button {  
    private List<CommonButton> sub_button;

    public List<CommonButton> getSub_button() {
        return sub_button;
    }

    public void setSub_button(List<CommonButton> sub_button) {
        this.sub_button = sub_button;
    }
}
