package com.compete.mis.models.viewmodels;

import java.io.Serializable;
import java.util.Map;

public class DifferentiatedSaveParameter extends ActionDataParameter implements Serializable {
    private Map<String, SaveData> data;

    public Map<String, SaveData> getData() {
        return data;
    }

    public void setData(Map<String, SaveData> data) {
        this.data = data;
    }
}
