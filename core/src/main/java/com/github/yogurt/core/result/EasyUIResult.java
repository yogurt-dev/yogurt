package com.github.yogurt.core.result;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties({"data","recordsTotal"})
public class EasyUIResult<T> extends QueryResult<T> {
    public EasyUIResult() {
    }

    public EasyUIResult(List<T> data) {
        super(data);
    }

    public List<T> getRows() {
        return super.getData();
    }

    public long getTotal() {
        return super.getRecordsTotal();
    }
}


