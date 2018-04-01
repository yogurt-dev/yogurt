package com.github.jyoghurt.sms;

import com.github.jyoghurt.core.exception.BaseAccidentException;
import com.github.jyoghurt.core.exception.ExceptionBody;

/**
 * Created by jtwu on 2015/12/25.
 */
public class SmsException extends BaseAccidentException{
    public static final ExceptionBody ERROR_91100 = new ExceptionBody(SmsExceptionEnums.ERROR_91100);

    SmsException(ExceptionBody exceptionBody,Object ...objects){
        super(exceptionBody,objects);
    }
    SmsException(ExceptionBody exceptionBody,Throwable e,Object ...objects){
        super(exceptionBody,e,objects);
    }
}
