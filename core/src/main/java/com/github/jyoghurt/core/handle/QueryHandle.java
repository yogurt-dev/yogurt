package com.github.jyoghurt.core.handle;

import com.github.jyoghurt.core.configuration.PageConvert;
import com.github.jyoghurt.core.utils.SpringContextUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by jtwu on 2015/1/13.
 */
public class QueryHandle {

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
    private Boolean distinct = false;
    //扩展的whereSql
    private List<String> whereSqls = new ArrayList<>();
    //扩展数据
    private Map<String, Object> expandData = new HashMap<>();
    //自定义sql开关，如果自定义sql打开
    private LinkedList<CustomWhereHandle> customList = new LinkedList<>();
    private String groupBy;

    public QueryHandle addGroupBy(String groupBy){
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
        customList.add(new CustomWhereHandle(CustomWhereHandle.TYPE.SQL,sql));
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
        customList.add(new CustomWhereHandle(CustomWhereHandle.TYPE.SQL, sql));
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

    public int getRows() {
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
        try {
            (SpringContextUtils.getBean(PageConvert.class)).convert(this, getHttpServletRequest());
        } catch (NoSuchBeanDefinitionException e) {
        }
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
}

