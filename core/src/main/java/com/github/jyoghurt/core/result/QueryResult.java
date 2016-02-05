package com.github.jyoghurt.core.result;

import com.fasterxml.jackson.annotation.JsonView;

import java.util.List;

/**
 * 查询结果
 *
 * @author jtwu
 */
public class QueryResult<T> {

    /**
     * 结果集
     */
    private List<T> data;

    /**
     * 总数
     */
    private long recordsTotal;

    public QueryResult() {
    }

    public QueryResult(List<T> data) {
        this.data = data;
        if(null!=data){
            this.recordsTotal = data.size();
        }
    }

    @JsonView(BaseResult.class)
    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    @JsonView(BaseResult.class)
    public long getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(long recordsTotal) {
        this.recordsTotal = recordsTotal;
    }
}
