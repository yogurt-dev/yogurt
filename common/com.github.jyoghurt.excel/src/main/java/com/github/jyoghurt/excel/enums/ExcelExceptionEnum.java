package com.github.jyoghurt.excel.enums;

public enum ExcelExceptionEnum {

    ERROR_401("导入数据中存在数据格式不规范，请确认后再导入！"),
    ERROR_402("Excel读取异常！");

    private String message;

    ExcelExceptionEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
