/**
 * ======================================
 * Copyright © Compete software 2017
 * ======================================
 * Author    Date       Time  Description
 * --------------------------------------
 * Lee Zheng 2017/10/2 15:59 Create.
 * ======================================
 */
package com.compete.mis.models.viewmodels;

import java.io.Serializable;

/**
 * 登录视图模型类。
 * @Author: Lee Zheng.
 */
public final class LoginViewModel implements Serializable {

    /**
     * 租户编码。
     */
    private String tenant;

    /**
     * 用户编码。
     */
    private  String user;

    /**
     * 密码。
     */
    private  String password;

    /**
     * 取得租户编码。
     * @return 租户编码。
     */
    public String getTenant() {
        return tenant;
    }

    /**
     * 设置租户编码。
     * @param tenant 租户编码。
     */
    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    /**
     * 取得用户编码。
     * @return
     */
    public String getUser() {
        return user;
    }

    /**
     * 设置用户编码。
     * @param user 用户编码。
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * 取得密码。
     * @return 密码。
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码。
     * @param password 密码。
     */
    public void setPassword(String password) {
        this.password = password;
    }

}
