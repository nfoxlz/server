package com.compete.mis.runtime;

import com.compete.mis.models.Tenant;

public final class Session {
    private Session() {
    }

    private static final ThreadLocal<Tenant> tenant = new ThreadLocal<>();

    private static final ThreadLocal<Long> user = new ThreadLocal<>();

    private static final ThreadLocal<String> sign = new ThreadLocal<>();

    public static Tenant getTenant() {
        return tenant.get();
    }

    public static void setTenant(Tenant tenant) {
        Session.tenant.set(tenant);
    }

    public static long getUser() {
        Long userId = user.get();
        return null == userId ? 0L : userId;
    }

    public static void setUser(long user) {
        Session.user.set(user);
    }

    public static String getSign() {
        return sign.get();
    }

    public static void setSign(String sign) {
        Session.sign.set(sign);
    }
}
