package com.compete.mis.models.viewmodels;

import java.io.Serializable;

public final class PagingQueryResult extends QueryResult implements Serializable {

    private long count;

    private long pageNo;

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
