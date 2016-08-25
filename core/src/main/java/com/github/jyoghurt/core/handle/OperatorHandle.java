package com.github.jyoghurt.core.handle;

/**
 * Created by jtwu on 2015/6/9.
 * sql 运算符
 */
public class OperatorHandle {

    /* modify by limiao 20160203 新增 NOT_EQUAL, NOT_LIKE,IS_NULL ,IS_NOT_NULL */
    /* modify by limiao 20160825 新增 NOT_IN*/
    public enum operatorType {
        EQUAL, LIKE, LESS_THEN, MORE_THEN, LESS_EQUAL, MORE_EQUAL, IN, NOT_IN, FIND_IN_SET, NOT_EQUAL, NOT_LIKE, IS_NULL, IS_NOT_NULL
    }

    private String column;
    private operatorType operator;
    private Object[] values;

    public OperatorHandle() {
    }

    public OperatorHandle(String column, operatorType operator) {
        this.column = column;
        this.operator = operator;
    }

    public OperatorHandle(String column, operatorType operator, Object[] values) {
        this.column = column;
        this.operator = operator;
        this.values = values;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public operatorType getOperator() {
        return operator;
    }

    public void setOperator(operatorType operator) {
        this.operator = operator;
    }

    public Object[] getValues() {
        return values;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }
}
