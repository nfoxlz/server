package com.compete.mis.models.viewmodels;

import java.io.Serializable;

public class Result implements Serializable {
    private int errorNo = 0;

    private String message;

    public int getErrorNo() {
        return errorNo;
    }

    public void setErrorNo(int errorNo) {
        this.errorNo = errorNo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
