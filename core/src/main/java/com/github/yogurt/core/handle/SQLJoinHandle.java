package com.github.yogurt.core.handle;

/**
 * Created by Administrator on 2015/6/8.
 * sql join 辅助类
 */
public class SQLJoinHandle {
    public static enum JoinType{
        JOIN,INNER_JOIN,LEFT_OUTER_JOIN,RIGHT_OUTER_JOIN,OUTER_JOIN
    }

    /**
     * 查询的列
     */
    private String selectColumns;
    /**
     * 链接类型
     */
    private JoinType joinType;
    /**
     * join sql
     */
    private String joinSql;

    public SQLJoinHandle() {
    }

    public SQLJoinHandle(String selectColumns, JoinType joinType, String joinSql) {
        this.selectColumns = selectColumns;
        this.joinType = joinType;
        this.joinSql = joinSql;
    }

    public SQLJoinHandle(JoinType joinType, String joinSql) {
        this.joinType = joinType;
        this.joinSql = joinSql;
    }

    public String getSelectColumns() {
        return selectColumns;
    }

    public SQLJoinHandle setSelectColumns(String selectColumns) {
        this.selectColumns = selectColumns;
        return this;
    }

    public JoinType getJoinType() {
        return joinType;
    }

    public SQLJoinHandle setJoinType(JoinType joinType) {
        this.joinType = joinType;
        return this;
    }

    public String getJoinSql() {
        return joinSql;
    }

    public SQLJoinHandle setJoinSql(String joinSql) {
        this.joinSql = joinSql;
        return this;
    }
}
