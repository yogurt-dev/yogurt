package com.github.jyoghurt.excel.exception;


import com.github.jyoghurt.excel.enums.ExcelExceptionEnum;
import com.github.jyoghurt.core.exception.BaseAccidentException;
import com.github.jyoghurt.core.exception.ExceptionBody;

public class ParseExcelException extends BaseAccidentException {

    public ParseExcelException(ExcelExceptionEnum excelExceptionEnum) {
        super(new ExceptionBody(excelExceptionEnum.name(), excelExceptionEnum.getMessage()));
    }

    public ParseExcelException(ExcelExceptionEnum excelExceptionEnum, Exception e) {
        super(new ExceptionBody(excelExceptionEnum.name(), excelExceptionEnum.getMessage()), e);
    }

    public ParseExcelException(Exception e) {
        super(new ExceptionBody(ExcelExceptionEnum.ERROR_402.name(), ExcelExceptionEnum.ERROR_402.getMessage()), e);
    }
}
