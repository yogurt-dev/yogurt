package com.github.jyoghurt.core.configuration.impl;

import com.github.jyoghurt.core.configuration.PageConvert;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.result.EasyUIResult;
import com.github.jyoghurt.core.result.QueryResult;
import com.github.jyoghurt.core.utils.SpringContextUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Administrator on 2015/8/28.
 */
public class EasyUIPageService implements Condition,PageConvert {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        return PageConvert.EasyUI.equalsIgnoreCase(SpringContextUtils.getProperty("tableJsLib"));
    }

    @Override
    public void convert(QueryHandle queryHandle, HttpServletRequest request) {

    }

    @Override
    public QueryResult createQueryResult() {
        return new EasyUIResult();
    }

    @Override
    public QueryResult createQueryResult(List list) {
        return new EasyUIResult(list);
    }
}
