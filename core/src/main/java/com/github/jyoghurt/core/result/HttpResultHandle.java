package com.github.jyoghurt.core.result;

/**
 * Created by jtwu on 2015/9/22.
 */

public class HttpResultHandle {

    public enum HttpResultEnum {
        NOT_LOGGED("-1", "未登录"), ERROR("0", "操作失败"), SUCCESS("1", "操作成功");
        private String errorCode;
        private String message;

        HttpResultEnum(String errorCode, String message) {
            this.errorCode = errorCode;
            this.message = message;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static HttpResultEntity<?> getSuccessResult() {
        return getSuccessResult(null);
    }

    public static HttpResultEntity<?> getSuccessResult(Object result) {
        return new HttpResultEntity<>(HttpResultEnum.SUCCESS.errorCode, HttpResultEnum.SUCCESS.message, result);
    }

    public static HttpResultEntity<?> getErrorResult() {
        return getErrorResult(null);
    }

    public static HttpResultEntity<?> getErrorResult(Object result) {
            return new HttpResultEntity<>(HttpResultEnum.ERROR.errorCode, HttpResultEnum.ERROR.message, result);
        }
    public static HttpResultEntity<?> getErrorResult(String errorCode,String message) {
        return new HttpResultEntity<>(errorCode, message);
    }
    public static HttpResultEntity<?> getErrorResult(String errorCode,String message,Object result) {
        return new HttpResultEntity<>(errorCode, message, result);
    }
}
