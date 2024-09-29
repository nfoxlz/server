package com.compete.mis.models.viewmodels;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SaveParameter extends ActionDataParameter implements Serializable {

    private List<SimpleDataTable> data;

    public List<SimpleDataTable> getData() {
        return data;
    }

    public void setData(List<SimpleDataTable> data) {
        this.data = data;
    }
}
