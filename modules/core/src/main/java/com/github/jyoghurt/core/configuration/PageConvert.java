package com.github.jyoghurt.core.configuration;

import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.result.QueryResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by Administrator on 2015/7/21.
 * 翻页信息转换器，前台框架的分页标示不统一，在此做适配
 */
public interface PageConvert<T> {
    String DataTable = "DataTable";
    String EasyUI= "EasyUI";
    String Donkish="Donkish";
     void convert(QueryHandle queryHandle, HttpServletRequest request);

    QueryResult createQueryResult();

    QueryResult<T> createQueryResult(List<T> list);
}
