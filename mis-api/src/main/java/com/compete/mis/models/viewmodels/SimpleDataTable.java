package com.compete.mis.models.viewmodels;

import java.io.Serializable;

public final class SimpleDataTable implements Serializable {

    private String[] columns;

    private Object[][] rows;

    public String[] getColumns() {
        return columns;
    }

    public void setColumns(String[] columns) {
        this.columns = columns;
    }

    public Object[][] getRows() {
        return rows;
    }

    public void setRows(Object[][] rows) {
        this.rows = rows;
    }
}
