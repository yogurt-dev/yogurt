package com.github.yogurt.core.result;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Created by Administrator on 2015/2/6.
 * http请求返回结果
 */

public class HttpResultEntity<R> {
    //结果编码
    private String errorCode;
    //结果信息
    private String message;
    //结果集
    private R result;

    public HttpResultEntity() {
    }

    public HttpResultEntity(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public HttpResultEntity(String errorCode, String message, R result) {
        this.errorCode = errorCode;
        this.message = message;
        this.result = result;
    }

    public HttpResultEntity(HttpResultHandle.HttpResultEnum httpResultEnum){
        this.errorCode = httpResultEnum.getErrorCode();
        this.message = httpResultEnum.name();
    }
    @JsonView(BaseResult.class)
    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    @JsonView(BaseResult.class)
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    @JsonView(BaseResult.class)
    public R getResult() {
        return result;
    }

    public void setResult(R result) {
        this.result = result;
    }
}
