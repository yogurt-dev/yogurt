package com.github.jyoghurt.core.result;


import com.github.jyoghurt.core.constant.Constant;
import com.github.jyoghurt.core.utils.StringUtils;

/**
 * Created by jtwu on 2015/9/22.
 */

public class HttpResultHandle {
    public static final String NOT_LOGGED_CODE = "-1";
    public static final String ERROR_CODE = "0";
    public static final String SUCCESS_CODE = "1";

    public enum HttpResultEnum {

        NOT_LOGGED(NOT_LOGGED_CODE), ERROR(ERROR_CODE), SUCCESS(SUCCESS_CODE);

        private String errorCode;

        HttpResultEnum(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }
    }

    public static HttpResultEntity<?> getSuccessResult() {
        return getSuccessResult(null);
    }

    public static HttpResultEntity<?> getSuccessResult(Object result) {
        return new HttpResultEntity<>(HttpResultEnum.SUCCESS.errorCode, HttpResultEnum.SUCCESS.name(), result);
    }

    public static HttpResultEntity<?> getErrorResult() {
        return new HttpResultEntity<>(HttpResultEnum.ERROR.errorCode, HttpResultEnum.ERROR.name());
    }

    public static HttpResultEntity<?> getErrorResult(Object result) {
        return new HttpResultEntity<>(HttpResultEnum.ERROR.errorCode, HttpResultEnum.ERROR.name(), result);
    }

    public static HttpResultEntity<?> getErrorResult(String message) {
        return new HttpResultEntity<>(HttpResultEnum.ERROR.errorCode, message);
    }

    public static HttpResultEntity<?> getErrorResult(String errorCode, String message) {
        return new HttpResultEntity<>(StringUtils.replaceAllToEmpty(errorCode, Constant.ERROR_CODE_PREFIX), message);
    }

    public static HttpResultEntity<?> getErrorResult(String errorCode, String message, Object result) {
        return new HttpResultEntity<>(StringUtils.replaceAllToEmpty(errorCode, Constant.ERROR_CODE_PREFIX), message, result);
    }
}
