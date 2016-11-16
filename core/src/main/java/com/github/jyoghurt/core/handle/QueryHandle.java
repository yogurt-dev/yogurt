package com.github.jyoghurt.core.handle;

import com.github.jyoghurt.core.configuration.impl.PageConfiguration;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.core.utils.DateTimeFormatter;
import com.github.jyoghurt.core.utils.JPAUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by jtwu on 2015/1/13.
 */
public class QueryHandle {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String AND = " AND ";
    private static final String OR = " OR ";

    private static final Boolean SENIOR_SEARCH = true;
    private static final Boolean COMMON_SEARCH = false;

    // 当前页
    private int page = 1;
    // 页面大小
    private int rows = 10;

    // 排序字段集合
    private LinkedHashMap<String, String> orderBy = new LinkedHashMap<String, String>();
    //sql join 辅助类
    private LinkedList<SQLJoinHandle> sqlJoinHandle = new LinkedList<>();
    //sql 操作符辅助类
    private Map<String, OperatorHandle> operatorHandles = new HashMap();
    //是否使用distinct
    private Boolean distinct;
    //扩展的whereSql
    private List<String> whereSqls = new ArrayList<>();
    //扩展数据
    private Map<String, Object> expandData = new HashMap<>();
    //自定义sql开关，如果自定义sql打开
    private LinkedList<CustomWhereHandle> customList = new LinkedList<>();
    private String groupBy;

    //如果是null则是or like，如果是1则是 and like
    private Boolean seniorSearch;

    /**
     * 设置高级查询方法 add by limiao 20160214
     *
     * @param object 高级查询涉及的对象
     * @return QueryHandle
     * @
     */
    public QueryHandle seniorSearch(Object object) {
        Assert.notNull(object, "seniorSearch -> object can not be null!");
        if (seniorSearch != null) {
            customWhereSql(this.getSeniorSearchWhereSql(this.getSeniorSearchSqlOperate(), object));
        }
        return this;
    }

    /**
     * 处理联合字段查询
     *
     * @return QueryHandle
     */
    public QueryHandle joinColumnsSearch() {
        customWhereSql(this.getJoinColumnsSearchWhereSql());
        return this;
    }

    /**
     * 获取联合字段查询sql
     *
     * @return String sql
     */
    public String getJoinColumnsSearchWhereSql() {
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
        String[] fieldNames = joinColumnNames.split(",");
        StringBuilder sb = new StringBuilder();
        for (String filedName : fieldNames) {
            addExpandData(filedName, joinColumnValue);
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
        customWhereSql(this.getDateBetweenSearchWhereSql());
        return this;
    }


    /**
     * 获取日期范围查询sql
     *
     * @return String sql
     */
    public String getDateBetweenSearchWhereSql() {
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


    /**
     * 根据高级搜索的条件获取拼接sql的操作符
     * add by limiao 20160214
     *
     * @return String AND或OR
     */
    private String getSeniorSearchSqlOperate() {
        if (SENIOR_SEARCH && seniorSearch) {
            return AND;
        } else {
            return OR;
        }
    }

    /**
     * 根据高级搜索标识和查询对象获取高级查询的sql add by limiao 20160214
     *
     * @param sqlOperate OR 或者 AND
     * @param object     高级查询涉及的对象
     * @return String sql
     * @
     */
    //todo 异常修改成 UI异常
    //todo 优化if else
    public String getSeniorSearchWhereSql(String sqlOperate, Object object) {
        StringBuilder sb = new StringBuilder();
        /* init request and validate */
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (request == null) {
            throw new IllegalArgumentException("getSeniorSearchWhereSql -> request can not be null!");
        }
        /* 使用反射获取查询字段，拼sql */
        List<Field> fieldList = JPAUtils.getAllFields(object.getClass());
        for (Field field : fieldList) {
            /*  如果是日期类型，那么去dateQueryParamsMap获取时间段条件 */
            if (JPAUtils.fieldIsDateType(field)) {
                /* 开始时间传值处理 */
                String expandFieldName = field.getName() + "_start";
                String start_time = request.getParameter(expandFieldName);
                if (StringUtils.isNotEmpty(start_time)) {
                    sb = appendLargerEqualThanSql(sb, sqlOperate, field.getName(), expandFieldName, start_time);
                }
                 /* 结束时间传值处理 */
                expandFieldName = field.getName() + "_end";
                String end_time = request.getParameter(expandFieldName);
                if (StringUtils.isNotEmpty(end_time)) {
                    sb = appendLessEqualThanSql(sb, sqlOperate, field.getName(), expandFieldName, end_time);
                }
            } else {
                /* 如果不是日期类型，字段的值不为null，String类型的字段拼like,其他类型的字段拼=号 */
                Object value = JPAUtils.getValue(object, field);
                if (value == null || "".equals(value)) {
                    continue;
                }
                if (JPAUtils.fieldIsStringType(field)) {
                    sb = appendLikeSql(sb, sqlOperate, field.getName());
                } else {
                    sb = appendEqualsSql(sb, sqlOperate, field.getName());
                }
            }
        }
        /* 如果sql不为空，那么去掉第一个or或and，然后把sql括起来，否者返回null */
        if (StringUtils.isNotEmpty(sb.toString())) {
            return "(" + sb.toString().replaceFirst(sqlOperate, "") + ")";
        }
        return null;
    }

    private StringBuilder appendLikeSql(StringBuilder sb, String sqlOperate, String fieldName) {
        return sb.append(sqlOperate).append(" t.").append(fieldName).append(" like CONCAT('%'," +
                "#{" + StringUtils.join(BaseMapper.DATA + "." + fieldName) + "}" + ", '%')");
    }

    private StringBuilder appendEqualsSql(StringBuilder sb, String sqlOperate, String fieldName) {
        return sb.append(sqlOperate).append(" t.").append(fieldName).append("= ").append("#{" +
                StringUtils.join(BaseMapper.DATA + "." + fieldName) + "}");
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

    public QueryHandle customWhereField(String fieldName) {
        customList.add(new CustomWhereHandle(CustomWhereHandle.TYPE.FIELD, fieldName));
        return this;
    }

    public QueryHandle customWhereField(String fieldName, OperatorHandle.operatorType operatorType, Object... values) {
        customList.add(new CustomWhereHandle(CustomWhereHandle.TYPE.FIELD, fieldName));
        addOperatorHandle(fieldName, operatorType, values);
        return this;
    }


    public QueryHandle customWhereField(String sql, CustomWhereHandle.TYPE type) {
        customList.add(new CustomWhereHandle(CustomWhereHandle.TYPE.SQL, sql));
        return this;
    }


    public QueryHandle OR() {
        customList.add(new CustomWhereHandle(CustomWhereHandle.TYPE.OR, null));
        return this;
    }

    public QueryHandle AND() {
        customList.add(new CustomWhereHandle(CustomWhereHandle.TYPE.AND, null));
        return this;
    }

    public QueryHandle customWhereSql(String sql) {
        //增加了不为null的判断 modify by limiao 20160216
        if (sql != null) {
            customList.add(new CustomWhereHandle(CustomWhereHandle.TYPE.SQL, sql));
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
        whereSqls.add(whereSql);
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

    public Boolean getSeniorSearch() {
        return seniorSearch;
    }

    public void setSeniorSearch(Boolean seniorSearch) {
        this.seniorSearch = seniorSearch;
    }
}

