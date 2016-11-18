package com.github.jyoghurt.core.configuration.impl;

import com.github.jyoghurt.core.configuration.PageConvert;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.result.DataTableResult;
import com.github.jyoghurt.core.result.QueryResult;
import com.github.jyoghurt.core.utils.SpringContextUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Administrator on 2015/8/28.
 */
public class DonkishPageService<T> implements Condition,PageConvert<T> {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        return PageConvert.Donkish.equalsIgnoreCase(SpringContextUtils.getProperty("tableJsLib"));
    }

    @Override
    public void convert(QueryHandle queryHandle, HttpServletRequest request) {

        String length = request.getParameter("iDisplayLength");
        if(StringUtils.isNotEmpty(length)){
            queryHandle.setRows(Integer.valueOf(length));
        }
        String start = request.getParameter("iDisplayStart");
        if(StringUtils.isNotEmpty(start)){
            queryHandle.setPage(Integer.valueOf(start)/ queryHandle.getRows()+1);
        }
        //处理排序
        if(StringUtils.isNotEmpty(request.getParameter("iSortCol_0"))&&"true".equals(request.getParameter
                ("bSortable_"+request.getParameter("iSortCol_0")))){
            queryHandle.addOrderBy(request.getParameter("mDataProp_"+request.getParameter("iSortCol_0")),
                    request.getParameter("sSortDir_0"));
        }
        //add by limiao 20161116 处理时间范围查询和联合查询
        //queryHandle.dateBetweenSearch().joinColumnsSearch();
    }

    @Override
    public QueryResult createQueryResult() {
        return new DataTableResult();
    }

    @Override
    public QueryResult<T> createQueryResult(List<T> list) {
        return new DataTableResult<>(list);
    }
}
