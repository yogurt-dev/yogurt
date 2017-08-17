package com.github.jyoghurt.image.exception;

import com.github.jyoghurt.image.enums.ImgExceptionEnums;
import com.github.jyoghurt.core.exception.BaseAccidentException;
import com.github.jyoghurt.core.exception.ExceptionBody;

/**
 * Created by think on 2016/10/20.
 */
public class ImgException extends BaseAccidentException {
    public static final ExceptionBody ERROR_9001 = new ExceptionBody(ImgExceptionEnums.ERROR_9001);
    public static final ExceptionBody ERROR_9002 = new ExceptionBody(ImgExceptionEnums.ERROR_9002);
    public static final ExceptionBody ERROR_9003 = new ExceptionBody(ImgExceptionEnums.ERROR_9003);
    public ImgException() {
        super(ERROR_9001);
    }

    public ImgException(ExceptionBody exceptionBody,Object ...objects){
        super(exceptionBody,objects);
    }

    public ImgException(ExceptionBody exceptionBody,Throwable e,Object ...objects){
        super(exceptionBody,e,objects);
    }

    public ImgException(ExceptionBody exceptionBody){
        super(exceptionBody);
    }
}
