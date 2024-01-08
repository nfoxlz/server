package com.compete.mis.models.viewmodels;

import java.io.Serializable;

public final class EnumInfo implements Serializable {

    private String name;

    private short value;

    private String displayName;

    private long sn;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getValue() {
        return value;
    }

    public void setValue(short value) {
        this.value = value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getSn() {
        return sn;
    }

    public void setSn(long sn) {
        this.sn = sn;
    }
}
