package com.github.jyoghurt.core.configuration.impl;

import com.github.jyoghurt.core.configuration.PageConvert;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.result.DataTableResult;
import com.github.jyoghurt.core.result.QueryResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/8/28.
 */
public class DataTablePageService<T> implements Condition,PageConvert<T> {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        return PageConvert.DataTable.equalsIgnoreCase(ResourceBundle.getBundle("environment-config").getString("tableJsLib"));
    }

    @Override
    public void convert(QueryHandle queryHandle, HttpServletRequest request) {

        String length = request.getParameter("length");
        if(StringUtils.isNotEmpty(length)){
            queryHandle.setRows(Integer.valueOf(length));
        }
        String start = request.getParameter("start");
        if(StringUtils.isNotEmpty(start)){
            queryHandle.setPage(Integer.valueOf(start)/ queryHandle.getRows()+1);
        }
        //处理排序
        for(String key : request.getParameterMap().keySet()){
            if (Pattern.compile("order\\[\\d*\\]\\[column\\]").matcher(key).find()){
                if(!"true".equals(request.getParameter("columns["+request.getParameter(key)+"][orderable]"))){
                    continue;
                }
                queryHandle.addOrderBy(request.getParameter("columns["+request.getParameter(key)+"][data]"),
                        request.getParameter("order[0][dir]"));
            }

        }

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
