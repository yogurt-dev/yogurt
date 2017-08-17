package com.github.jyoghurt.image.enums;

/**
 * @Description:
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2016-11-02 11:20
 */
public enum ImgExceptionEnums {
    ERROR_9001("上传文件过大，不能超过1M"),
    ERROR_9002("上传文件失败"),
    ERROR_9003("系统不支持此类型图片"),
    ERROR_9004("切图错误");
    private String message;

    public String getMessage() {
        return message;
    }

    public ImgExceptionEnums setMessage(String message) {
        this.message = message;
        return this;
    }


    ImgExceptionEnums(String errorCode) {
        this.message = errorCode;
    }


}
