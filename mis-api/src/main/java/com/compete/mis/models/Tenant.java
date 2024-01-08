/**
 * ======================================
 * Copyright © Compete software 2017
 * ======================================
 * Author    Date       Time  Description
 * --------------------------------------
 * Lee Zheng 2017/10/2 15:59 Create.
 * ======================================
 */
package com.compete.mis.models;

import java.io.Serializable;

/**
 * 租户类。
 * @Author: Lee Zheng.
 */
public final class Tenant extends Entity implements Serializable {

    /**
     * 数据库服务器名。
     */
    private String dbServerName;

    /**
     * 只读数据库服务器名称。
     */
    private String readOnlyDbServerName;

    /**
     * 取得数据库服务器名。
     * @return 数据库服务器名。
     */
    public String getDbServerName() {
        return dbServerName;
    }

    /**
     * 取得数据库服务器名。
     * @return 数据库服务器名。
     */
    public String getDbServerName(final boolean isReadOnly) {
        return isReadOnly ? readOnlyDbServerName : dbServerName;
    }

    /**
     * 设置数据库服务器名。
     * @param dbServerName 数据库服务器名。
     */
    public void setDbServerName(final String dbServerName) {
        this.dbServerName = dbServerName;
    }

    /**
     * 取得只读数据库服务器名称。
     * @return 只读数据库服务器名称。
     */
    public String getReadOnlyDbServerName() {
        return readOnlyDbServerName;
    }

    /**
     * 设置只读数据库服务器名称。
     * @param readOnlyDbServerName 只读数据库服务器名称。
     */
    public void setReadOnlyDbServerName(final String readOnlyDbServerName) {
        this.readOnlyDbServerName = readOnlyDbServerName;
    }
}
