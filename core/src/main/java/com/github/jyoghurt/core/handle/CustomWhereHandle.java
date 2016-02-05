package com.github.jyoghurt.core.handle;

/**
 * Created by jtwu on 2015/10/29.
 */
public class CustomWhereHandle {
    public enum TYPE {
        FIELD, SQL, OR, AND
    }

    public CustomWhereHandle(TYPE type, String sql) {
        this.type = type;
        this.sql = sql;
    }

    private TYPE type;
    private String sql;

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
