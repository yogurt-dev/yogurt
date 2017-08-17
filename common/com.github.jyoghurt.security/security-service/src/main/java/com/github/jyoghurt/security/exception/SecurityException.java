package com.github.jyoghurt.security.exception;

import com.github.jyoghurt.security.enums.SecurityExceptionEnum;
import com.github.jyoghurt.core.exception.BaseAccidentException;
import com.github.jyoghurt.core.exception.ExceptionBody;

/**
 * @Project: 驴鱼社区-车险帮
 * @Package: com.df.security.exception
 * @Description:
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2016-12-08 10:23
 */
public class SecurityException extends BaseAccidentException{

    public static final ExceptionBody ERROR_90801 = new ExceptionBody(SecurityExceptionEnum.ERROR_90801);
    public static final ExceptionBody ERROR_90802 = new ExceptionBody(SecurityExceptionEnum.ERROR_90802);
    public static final ExceptionBody ERROR_90803 = new ExceptionBody(SecurityExceptionEnum.ERROR_90803);
    public static final ExceptionBody ERROR_90804 = new ExceptionBody(SecurityExceptionEnum.ERROR_90804);
    public static final ExceptionBody ERROR_90805 = new ExceptionBody(SecurityExceptionEnum.ERROR_90805);
    public static final ExceptionBody ERROR_90806 = new ExceptionBody(SecurityExceptionEnum.ERROR_90806);
    public static final ExceptionBody ERROR_90807 = new ExceptionBody(SecurityExceptionEnum.ERROR_90807);


    public SecurityException(ExceptionBody exceptionBody,Object ...objects){
        super(exceptionBody);
    }

    public SecurityException(ExceptionBody exceptionBody,Throwable e){
        super(exceptionBody,e);
    }

    public SecurityException(ExceptionBody exceptionBody){
        super(exceptionBody);
    }

}
