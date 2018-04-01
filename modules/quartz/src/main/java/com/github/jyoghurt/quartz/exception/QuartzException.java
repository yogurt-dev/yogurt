package com.github.jyoghurt.quartz.exception;

import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.core.exception.ExceptionBody;

/**
 * user:dell
 * date: 2016/5/27.
 */
public class QuartzException extends BaseErrorException {
    private static final long serialVersionUID = 2657618496024743577L;

    /**
     * 验证异常
     */
    public static final ExceptionBody SYNCSCHEDULEJOB_EXCEPTION = new ExceptionBody("9001", "同步调度异常");
    /**
     * 调度器未实例化异常
     */
    public static final ExceptionBody SCHEDULEFACTORY_NOT_FOUND_EXCEPTION = new ExceptionBody("9002", "quartz调度器工厂未实例化");
    /**
     * 创建调度器异常
     */
    public static final ExceptionBody CREATE_SCHEDULER_EXCEPTION = new ExceptionBody("9004", "创建调度器异常");
    /**
     * 更新调度器异常
     */
    public static final ExceptionBody UPDATE_SCHEDULER_EXCEPTION = new ExceptionBody("9005", "更新调度器异常");
    /**
     * 调度器指定类未找到
     */
    public static final ExceptionBody SCHEDULEJOB_CLASS_NOT_FOUND_EXCEPTION = new ExceptionBody("9006", "调度器指定类未找到");
    /**
     * 删除调度异常
     */
    public static final ExceptionBody REMOVE_SCHEDULEJOB_EXCEPTION = new ExceptionBody("9007", "删除调度异常");
    /**
     * 禁用Job异常
     */
    public static final ExceptionBody FORBIDDENJOB_EXCEPTION = new ExceptionBody("9008", "禁用Job异常");
    /**
     * 调度器未知异常
     */
    public static final ExceptionBody SCHEDULEJOB_UNKNOW_EXCEPTION = new ExceptionBody("9999", "调度器未知异常");

    public QuartzException(ExceptionBody exceptionBody) {
        super(exceptionBody);
    }

    public QuartzException(ExceptionBody exceptionBody, Throwable cause) {
        super(exceptionBody, cause);
    }
}
