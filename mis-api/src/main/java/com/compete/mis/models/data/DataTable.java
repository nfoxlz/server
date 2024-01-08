package com.compete.mis.models.data;

import java.io.Serializable;
import java.util.List;

public final class DataTable implements Serializable {

    private String tableName;

    private List<DataColumn> columns;

    private List<List<?>> rows;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<DataColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<DataColumn> columns) {
        this.columns = columns;
    }

    public List<List<?>> getRows() {
        return rows;
    }

    public void setRows(List<List<?>> rows) {
        this.rows = rows;
    }
}
