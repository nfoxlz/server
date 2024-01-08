package com.compete.mis.models.viewmodels;

import java.io.Serializable;
import java.util.List;

public final class PagingQueryResult implements Serializable {

    private List<SimpleDataTable> data;

    private long count;

    private long pageNo;

    public List<SimpleDataTable> getData() {
        return data;
    }

    public void setData(List<SimpleDataTable> data) {
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
