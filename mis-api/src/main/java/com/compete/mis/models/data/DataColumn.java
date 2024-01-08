package com.compete.mis.models.data;

import java.io.Serializable;

public final class DataColumn implements Serializable {

    private String columnName;

    private int dataType;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }
}
