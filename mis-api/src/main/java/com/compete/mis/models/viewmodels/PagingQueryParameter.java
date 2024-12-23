package com.compete.mis.models.viewmodels;

import java.io.Serializable;

public final class PagingQueryParameter extends QueryParameter implements Serializable {

    private long currentPageNo;

    private short pageSize;

    private String sortDescription;

    public long getCurrentPageNo() {
        return currentPageNo;
    }

    public void setCurrentPageNo(long currentPageNo) {
        this.currentPageNo = currentPageNo;
    }

    public short getPageSize() {
        return pageSize;
    }

    public void setPageSize(short pageSize) {
        this.pageSize = pageSize;
    }

    public String getSortDescription() {
        return sortDescription;
    }

    public void setSortDescription(String sortDescription) {
        this.sortDescription = sortDescription;
    }
}
