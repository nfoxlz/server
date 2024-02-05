package com.compete.mis.models.viewmodels;

import java.io.Serializable;
import java.util.Map;

public final class PagingQueryResult implements Serializable {

    private Map<String, SimpleDataTable> data;

    private long count;

    private long pageNo;

    public Map<String, SimpleDataTable> getData() {
        return data;
    }

    public void setData(Map<String, SimpleDataTable> data) {
        this.data = data;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getPageNo() {
        return pageNo;
    }

    public void setPageNo(long pageNo) {
        this.pageNo = pageNo;
    }
}
