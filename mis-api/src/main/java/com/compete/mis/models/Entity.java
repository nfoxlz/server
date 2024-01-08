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
 * 实体类。
 * @Author: Lee Zheng.
 */
public class Entity implements Serializable {

    /**
     * 主键。
     */
    private long id;

    private String code;

    private String name;

    /**
     * 取得主键。
     * @return 主键。
     */
    public long getId() {
        return id;
    }

    /**
     * 设置主键。
     * @param id 主键。
     */
    public void setId(final long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
