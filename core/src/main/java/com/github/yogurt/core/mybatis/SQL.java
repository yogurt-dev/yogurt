package com.github.yogurt.core.mybatis;

import com.github.yogurt.core.handle.SQLJoinHandle;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.AbstractSQL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Clinton Begin
 * @author Jeff Butler
 * @author Adam Gent
 */

public class SQL extends AbstractSQL<SQL> {

    private static final String AND = ") \nAND (";
    private static final String OR = ") \nOR (";


    @Override
    public SQL getSelf() {
        return this;
    }

    public SQL UPDATE(String table) {
        sql().statementType = SQLStatement.StatementType.UPDATE;
        sql().tables.add(table);
        return getSelf();
    }

    public SQL SET(String sets) {
        sql().sets.add(sets);
        return getSelf();
    }

    public SQL INSERT_INTO(String tableName) {
        sql().statementType = SQLStatement.StatementType.INSERT;
        sql().tables.add(tableName);
        return getSelf();
    }

    public SQL BATCH_INSERT_INTO(String tableName) {
        sql().statementType = SQLStatement.StatementType.BATCH_INSERT;
        sql().tables.add(tableName);
        return getSelf();
    }

    public SQL BATCH_SEGMENTATION() {
        sql().columns.clear();
        sql().columns.addAll(sql().batchColumns);
        sql().batchColumns.clear();
        sql().values.add(StringUtils.join("(", StringUtils.join(sql().batchValues.toArray(), ","), ")"));
        sql().batchValues.clear();
        return getSelf();
    }

    public SQL VALUES(String columns, String values) {
        sql().columns.add(columns);
        sql().values.add(values);
        return getSelf();
    }

    public SQL BATCH_VALUES(String columns, String values) {
        sql().batchColumns.add(columns);
        sql().batchValues.add(values);
        return getSelf();
    }

    public SQL SELECT(String columns) {
        sql().statementType = SQLStatement.StatementType.SELECT;
        sql().select.add(columns);
        return getSelf();
    }

    public SQL SELECT_DISTINCT(String columns) {
        sql().distinct = true;
        SELECT(columns);
        return getSelf();
    }

    public SQL DELETE_FROM(String table) {
        sql().statementType = SQLStatement.StatementType.DELETE;
        sql().tables.add(table);
        return getSelf();
    }

    public SQL FROM(String table) {
        sql().tables.add(table);
        return getSelf();
    }

    public SQL JOIN(String join) {
        sql().sqlJoins.add(new SQLJoinHandle(SQLJoinHandle.JoinType.JOIN, join));
//        sql().join.add(join);
        return getSelf();
    }

    public SQL INNER_JOIN(String join) {
        sql().sqlJoins.add(new SQLJoinHandle(SQLJoinHandle.JoinType.INNER_JOIN, join));
//        sql().innerJoin.add(join);
        return getSelf();
    }

    public SQL LEFT_OUTER_JOIN(String join) {
        sql().sqlJoins.add(new SQLJoinHandle(SQLJoinHandle.JoinType.LEFT_OUTER_JOIN, join));
//        sql().leftOuterJoin.add(join);
        return getSelf();
    }

    public SQL RIGHT_OUTER_JOIN(String join) {
        sql().sqlJoins.add(new SQLJoinHandle(SQLJoinHandle.JoinType.RIGHT_OUTER_JOIN, join));
//        sql().rightOuterJoin.add(join);
        return getSelf();
    }

    public SQL OUTER_JOIN(String join) {
        sql().sqlJoins.add(new SQLJoinHandle(SQLJoinHandle.JoinType.OUTER_JOIN, join));
//        sql().outerJoin.add(join);
        return getSelf();
    }

    public SQL WHERE(String conditions) {
        sql().where.add(conditions);
        sql().lastList = sql().where;
        return getSelf();
    }

    public SQL OR() {
        sql().lastList.add(OR);
        return getSelf();
    }

    public SQL AND() {
        sql().lastList.add(AND);
        return getSelf();
    }

    public SQL GROUP_BY(String columns) {
        sql().groupBy.add(columns);
        return getSelf();
    }

    public SQL HAVING(String conditions) {
        sql().having.add(conditions);
        sql().lastList = sql().having;
        return getSelf();
    }

    public SQL ORDER_BY(String columns) {
        sql().orderBy.add(columns);
        return getSelf();
    }

    private SQLStatement sql = new SQLStatement();

    private SQLStatement sql() {
        return sql;
    }

    public <A extends Appendable> A usingAppender(A a) {
        sql().sql(a);
        return a;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sql().sql(sb);
        return sb.toString();
    }

    private static class SafeAppendable {
        private final Appendable a;
        private boolean empty = true;

        public SafeAppendable(Appendable a) {
            super();
            this.a = a;
        }

        public SafeAppendable append(CharSequence s) {
            try {
                if (empty && s.length() > 0) empty = false;
                a.append(s);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        public boolean isEmpty() {
            return empty;
        }

    }

    private static class SQLStatement {

        public enum StatementType {
            DELETE, INSERT, SELECT, UPDATE, BATCH_INSERT
        }

        StatementType statementType;
        List<String> sets = new ArrayList<String>();
        List<String> select = new ArrayList<String>();
        List<String> tables = new ArrayList<String>();
        List<SQLJoinHandle> sqlJoins = new ArrayList<SQLJoinHandle>();
        List<String> join = new ArrayList<String>();
        List<String> innerJoin = new ArrayList<String>();
        List<String> outerJoin = new ArrayList<String>();
        List<String> leftOuterJoin = new ArrayList<String>();
        List<String> rightOuterJoin = new ArrayList<String>();
        List<String> where = new ArrayList<String>();
        List<String> having = new ArrayList<String>();
        List<String> groupBy = new ArrayList<String>();
        List<String> orderBy = new ArrayList<String>();
        List<String> lastList = new ArrayList<String>();
        List<String> columns = new ArrayList<String>();
        List<String> values = new ArrayList<String>();
        List<String> batchValues = new ArrayList<>();
        List<String> batchColumns = new ArrayList<>();
        boolean distinct;

        private void sqlClause(SafeAppendable builder, String keyword, List<String> parts, String open, String close,
                               String conjunction) {
            if (!parts.isEmpty()) {
                if (!builder.isEmpty())
                    builder.append("\n");
                builder.append(keyword);
                builder.append(" ");
                builder.append(open);
                String last = "________";
                for (int i = 0, n = parts.size(); i < n; i++) {
                    String part = parts.get(i);
                    if (i > 0 && !part.equals(AND) && !part.equals(OR) && !last.equals(AND) && !last.equals(OR)) {
                        builder.append(conjunction);
                    }
                    builder.append(part);
                    last = part;
                }
                builder.append(close);
            }
        }

        private String selectSQL(SafeAppendable builder) {
            if (distinct) {
                sqlClause(builder, "SELECT DISTINCT", select, "", "", ", ");
            } else {
                sqlClause(builder, "SELECT", select, "", "", ", ");
            }

            sqlClause(builder, "FROM", tables, "", "", ", ");

            for (SQLJoinHandle sqlJoin : sqlJoins) {
                switch (sqlJoin.getJoinType()) {
                    case JOIN: {
                        sqlClause(builder, "JOIN", Arrays.asList(sqlJoin.getJoinSql()), "", "", "\nJOIN ");
                        break;
                    }
                    case INNER_JOIN: {
                        sqlClause(builder, "INNER JOIN", Arrays.asList(sqlJoin.getJoinSql()), "", "", "\nINNER JOIN ");
                        break;
                    }
                    case LEFT_OUTER_JOIN: {
                        sqlClause(builder, "LEFT OUTER JOIN", Arrays.asList(sqlJoin.getJoinSql()), "", "", "\nLEFT OUTER JOIN ");
                        break;
                    }
                    case RIGHT_OUTER_JOIN: {
                        sqlClause(builder, "RIGHT OUTER JOIN", Arrays.asList(sqlJoin.getJoinSql()), "", "", "\nRIGHT OUTER JOIN ");
                        break;
                    }
                    case OUTER_JOIN: {
                        sqlClause(builder, "OUTER JOIN", Arrays.asList(sqlJoin.getJoinSql()), "", "", "\nOUTER JOIN ");
                        break;
                    }

                }
            }
            sqlClause(builder, "WHERE", where, "(", ")", " AND ");
            sqlClause(builder, "GROUP BY", groupBy, "", "", ", ");
            sqlClause(builder, "HAVING", having, "(", ")", " AND ");
            sqlClause(builder, "ORDER BY", orderBy, "", "", ", ");
            return builder.toString();
        }

        private String insertSQL(SafeAppendable builder) {
            sqlClause(builder, "INSERT INTO", tables, "", "", "");
            sqlClause(builder, "", columns, "(", ")", ", ");
            sqlClause(builder, "VALUES", values, "(", ")", ", ");
            return builder.toString();
        }

        private String batchInsertSQL(SafeAppendable builder) {
            sqlClause(builder, "INSERT INTO", tables, "", "", "");
            sqlClause(builder, "", columns, "(", ")", ", ");
            sqlClause(builder, "VALUES", values, "", "", ", ");
            return builder.toString();
        }

        private String deleteSQL(SafeAppendable builder) {
            sqlClause(builder, "DELETE FROM", tables, "", "", "");
            sqlClause(builder, "WHERE", where, "(", ")", " AND ");
            return builder.toString();
        }

        private String updateSQL(SafeAppendable builder) {

            sqlClause(builder, "UPDATE", tables, "", "", "");
            sqlClause(builder, "SET", sets, "", "", ", ");
            sqlClause(builder, "WHERE", where, "(", ")", " AND ");
            return builder.toString();
        }

        public String sql(Appendable a) {
            SafeAppendable builder = new SafeAppendable(a);
            if (statementType == null) {
                return null;
            }

            String answer;

            switch (statementType) {
                case DELETE:
                    answer = deleteSQL(builder);
                    break;

                case INSERT:
                    answer = insertSQL(builder);
                    break;

                case BATCH_INSERT:
                    answer = batchInsertSQL(builder);
                    break;

                case SELECT:
                    answer = selectSQL(builder);
                    break;

                case UPDATE:
                    answer = updateSQL(builder);
                    break;

                default:
                    answer = null;
            }

            return answer;
        }
    }
}