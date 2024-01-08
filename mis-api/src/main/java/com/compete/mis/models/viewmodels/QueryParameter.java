package com.compete.mis.models.viewmodels;

import java.io.Serializable;
import java.util.Map;

public class QueryParameter extends DataParameter implements Serializable {
    private Map<String, ?> parameters;

    public Map<String, ?> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, ?> parameters) {
        this.parameters = parameters;
    }
}
