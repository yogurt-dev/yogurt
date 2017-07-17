package com.github.jyoghurt.core.handle;

import com.github.jyoghurt.core.configuration.impl.PageConfiguration;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.core.utils.DateTimeFormatter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jtwu on 2015/1/13.
 */
public class QueryHandle {

    private static final String joinColumn_ = "joinColumn_";

    private static final String AND = " AND ";
    private static final String OR = " OR ";

    // 当前页
    private int page = 1;
    // 页面大小
    private int rows = 10;

    // 排序字段集合
    private LinkedHashMap<String, String> orderBy = new LinkedHashMap<String, String>();
    //sql join 辅助类
    private LinkedList<SQLJoinHandle> sqlJoinHandle = new LinkedList<>();
    //sql 操作符辅助类
    private Map<String, OperatorHandle> operatorHandles = new ConcurrentHashMap();
    //是否使用distinct
    private Boolean distinct;
    //扩展的whereSql
    private List<String> whereSqls = new ArrayList<>();
    //扩展数据
    private Map<String, Object> expandData = new HashMap<>();

    private String groupBy;

    //处理查询列问题 add by limiao 20170508
    //指定查询列sql，例如“ t.name,t.pass”
    protected String selectColumnSql;

    /**
     * 已经拼上sql的字段集合
     */
    private Set<String> alreadyAppendFieldSet = new HashSet<>();

    /**
     * 新“高级”查询接口
     */
    public QueryHandle search() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (request == null) {
            throw new BaseErrorException("getSeniorSearchWhereSql -> request can not be null!");
        }
        this.joinColumnsSearch().dateBetweenSearch();
        return this;
    }


//    /**
//     * 获取普通字段查询sql
//     *
//     * @return String sql
//     */
//    private String getCommonColumnsSearchWhereSql(BaseEntity baseEntity) {
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        if (request == null) {
//            throw new BaseErrorException("getJoinColumnsSearchWhereSql -> request can not be null!");
//        }
//        List<Field> fieldList = JPAUtils.getAllFields(baseEntity.getClass());
//        StringBuilder sb = new StringBuilder();
//        for (Field field : fieldList) {
//            /*  如果是日期类型，那么去dateQueryParamsMap获取时间段条件 */
//            if (JPAUtils.fieldIsDateType(field) || alreadyAppendFieldSet.contains(field.getName())) {
//                continue;
//            }
//            Object value = JPAUtils.getValue(baseEntity, field);
//            if (value == null || "".equals(value)) {
//                continue;
//            }
//            sb = appendLikeSql(sb, AND, field.getName());
//        }
//        if (StringUtils.isNotEmpty(sb.toString())) {
//            return "(" + sb.toString().replaceFirst(AND, "") + ")";
//        }
//        return null;
//    }


    /**
     * 处理联合字段查询
     *
     * @return QueryHandle
     */
    public QueryHandle joinColumnsSearch() {
        addWhereSql(this.getJoinColumnsSearchWhereSql());
        return this;
    }

    /**
     * 获取联合字段查询sql
     *
     * @return String sql
     */
    private String getJoinColumnsSearchWhereSql() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (request == null) {
            throw new BaseErrorException("getJoinColumnsSearchWhereSql -> request can not be null!");
        }
        String joinColumnNames = request.getParameter("joinColumnNames");
        if (StringUtils.isEmpty(joinColumnNames)) {
            return null;
        }
        String joinColumnValue = request.getParameter("joinColumnValue");
        if (StringUtils.isEmpty(joinColumnValue)) {
            return null;
        }
        joinColumnValue = joinColumnValue.trim();
        String[] fieldNames = joinColumnNames.split(",");
        StringBuilder sb = new StringBuilder();
        for (String filedName : fieldNames) {
            alreadyAppendFieldSet.add(filedName);
            addExpandData(joinColumn_ + filedName, joinColumnValue);
            sb = appendLikeSql(sb, OR, filedName);
        }
        if (StringUtils.isNotEmpty(sb.toString())) {
            return "(" + sb.toString().replaceFirst(OR, "") + ")";
        }
        return null;
    }

    /**
     * 处理日期范围查询，如：字段createTime ，前台需要按规则传入createTime_start或createTime_end.
     * 目前支持 年月日格式，会自动把结束时间+1天。
     */
    public QueryHandle dateBetweenSearch() {
        addWhereSql(this.getDateBetweenSearchWhereSql());
        return this;
    }


    /**
     * 获取日期范围查询sql
     *
     * @return String sql
     */
    private String getDateBetweenSearchWhereSql() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (request == null) {
            throw new BaseErrorException("getSearchDateBetweenWhereSql -> request can not be null!");
        }
        String dateRangeColumnNames = request.getParameter("dateRangeColumnNames");
        if (StringUtils.isEmpty(dateRangeColumnNames)) {
            return null;
        }
        String[] columns = dateRangeColumnNames.split(",");
        StringBuilder sb = new StringBuilder();
        for (String filedName : columns) {
            String expandFieldName = filedName + "_start";
            String startTime = request.getParameter(expandFieldName);
            if (StringUtils.isNotEmpty(startTime)) {
                sb = appendLargerEqualThanSql(sb, AND, filedName, expandFieldName, startTime);
            }
            String endFieldName = filedName + "_end";
            String end_time = request.getParameter(endFieldName);
            if (StringUtils.isNotEmpty(end_time)) {
                sb = appendLessEqualThanSql(sb, AND, filedName, endFieldName, end_time);
            }
        }
        if (StringUtils.isNotEmpty(sb.toString())) {
            return "(" + sb.toString().replaceFirst(AND, "") + ")";
        }
        return null;
    }

    private StringBuilder appendLikeSql(StringBuilder sb, String sqlOperate, String fieldName) {
        return sb.append(sqlOperate).append(" t.").append(fieldName).append(" like CONCAT('%'," +
                "#{" + StringUtils.join(BaseMapper.DATA, ".", joinColumn_, fieldName) + "}" + ", '%')");
    }

    private StringBuilder appendLargerEqualThanSql(StringBuilder sb, String sqlOperate, String fieldName,
                                                   String expandFieldName, String value) {
        addExpandData(expandFieldName, value);
        return sb.append(sqlOperate).append(" t.").append(fieldName).append(">= ").append("#{" +
                StringUtils.join(BaseMapper.DATA + "." + expandFieldName) + "}");
    }

    private StringBuilder appendLessEqualThanSql(StringBuilder sb, String sqlOperate, String fieldName,
                                                 String expandFieldName, String value) {
        addExpandData(expandFieldName, value);
        sb.append(sqlOperate).append(" t.").append(fieldName);
        if (DateTimeFormatter.isYYYYMMddFormatDate(value)) {
            sb.append("<= date_format(addDate(").append("#{" + StringUtils.join(BaseMapper.DATA + "." + expandFieldName) + "},1),'%Y-%m-%d')");
        } else {
            sb.append("<= ").append("#{" + StringUtils.join(BaseMapper.DATA + "." + expandFieldName) + "}");
        }
        return sb;
    }

    public QueryHandle addGroupBy(String groupBy) {
        this.groupBy = groupBy;
        return this;
    }

    public QueryHandle setSelectColumnSql(String sql) {
        //增加了不为null的判断 modify by limiao 20160216
        if (StringUtils.isNotEmpty(sql)) {
            this.selectColumnSql = sql;
        }
        return this;
    }

    public QueryHandle addJoinHandle(SQLJoinHandle sqlJoinHandle) {
        this.sqlJoinHandle.add(sqlJoinHandle);
        return this;
    }

    public QueryHandle addJoinHandle(String selectColumns, SQLJoinHandle.JoinType joinType, String joinSql) {
        sqlJoinHandle.add(new SQLJoinHandle(selectColumns, joinType, joinSql));
        return this;
    }


    public QueryHandle addOrderBy(String key, String value) {
        orderBy.put(key, value);
        return this;
    }

    public int getPage() {
        return page;
    }

    public QueryHandle setPage(int page) {
        this.page = page;
        return this;
    }

    //分页数据处理，如果超过2000行则设置成默认20行 add by limiao on 2016/4/15.
    private static int maxRows = 2000;
    private static int resetRows = 20;

    //以下两个方法需要引用core处系统初始化时设置
    public static void setMaxRows(int maxRows_) {
        maxRows = maxRows_;
    }

    public static void setResetRows(int resetRows_) {
        resetRows = resetRows_;
    }

    public int getRows() {
        if (rows > maxRows) {
            rows = resetRows;
        }
        return rows;
    }

    public QueryHandle setRows(int rows) {
        this.rows = rows;
        return this;
    }

    public QueryHandle addOperatorHandle(String column, OperatorHandle.operatorType operatorType, Object...
            values) {
        if (StringUtils.isEmpty(column) || null == operatorType) {
            return this;
        }
        operatorHandles.put(column, new OperatorHandle(column, operatorType, values));
        return this;
    }

    public QueryHandle addWhereSql(String whereSql) {
        if (StringUtils.isNotEmpty(whereSql)) {
            whereSqls.add(whereSql);
        }
        return this;
    }

    public QueryHandle addSqlJoinHandle(String selectColumns, SQLJoinHandle.JoinType joinType, String joinSql) {
        if (StringUtils.isEmpty(joinSql) || null == joinType) {
            return this;
        }
        sqlJoinHandle.add(new SQLJoinHandle(selectColumns, joinType, joinSql));
        return this;
    }

    public QueryHandle configPage() {
        PageConfiguration.create().convert(this, getHttpServletRequest());
        return this;
    }

    public static HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes == null ? null : attributes.getRequest();
    }

    public QueryHandle addDistinct() {
        distinct = true;
        return this;
    }

    public Boolean getDistinct() {
        return distinct;
    }


    public QueryHandle addExpandData(String key, Object value) {
        expandData.put(key, value);
        return this;
    }

    public Map<String, Object> getExpandData() {
        return expandData;
    }

    public String getSelectColumnSql() {
        return selectColumnSql;
    }

}

