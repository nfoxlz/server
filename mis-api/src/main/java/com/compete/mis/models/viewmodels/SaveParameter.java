package com.compete.mis.models.viewmodels;

import java.io.Serializable;
import java.util.Map;

public class SaveParameter extends ActionDataParameter implements Serializable {
    private Map<String, SimpleDataTable> data;

    public Map<String, SimpleDataTable> getData() {
        return data;
    }

    public void setData(Map<String, SimpleDataTable> data) {
        this.data = data;
    }
}
