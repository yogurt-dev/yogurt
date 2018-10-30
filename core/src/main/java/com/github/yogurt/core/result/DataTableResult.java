package com.github.yogurt.core.result;

import java.util.List;

public class DataTableResult<T> extends QueryResult<T>{
    public DataTableResult() {
    }

    public DataTableResult(List<T> data) {
        super(data);
    }

    /**
     * 请求次数
     */
    private long draw;
    /**
     * 过滤后记录数
     */
    private long recordsFiltered;

    public long getRecordsFiltered() {
        return super.getRecordsTotal();
    }

    public long getDraw() {
        return draw;
    }
}
