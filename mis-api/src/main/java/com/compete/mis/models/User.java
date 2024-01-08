package com.compete.mis.models;

import java.io.Serializable;

public final class User extends Entity implements Serializable {

    private String userPassword;

    private Tenant tenant;

    private Entity role;

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(final String userPassword) {
        this.userPassword = userPassword;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(final Tenant tenant) {
        this.tenant = tenant;
    }

    public Entity getRole() {
        return role;
    }

    public void setRole(final Entity role) {
        this.role = role;
    }
}
