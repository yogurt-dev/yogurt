package com.github.jyoghurt.dataDict.exception;

import com.github.jyoghurt.core.exception.BaseAccidentException;
import com.github.jyoghurt.core.exception.ExceptionBody;


public class DataDictValueException extends BaseAccidentException {

    public static final ExceptionBody ERROR_1800= new ExceptionBody("1800", "数据字典值数据重复！");
    public static final ExceptionBody ERROR_1801 = new ExceptionBody("1801", "字典值名称不能为空！");
    public static final ExceptionBody ERROR_1802 = new ExceptionBody("1802", "字典值不能为空！");
    public static final ExceptionBody ERROR_1803 = new ExceptionBody("1803", "字典值名称重复，确认添加？");

    public DataDictValueException() {
        super(ERROR_1800);
    }

    public DataDictValueException(ExceptionBody exceptionBody) {
        super(exceptionBody);
    }

    public DataDictValueException(Throwable e) {
        super(ERROR_1800, e);
    }

    public DataDictValueException(ExceptionBody exceptionBody, Throwable e) {
        super(exceptionBody, e);
    }
}
