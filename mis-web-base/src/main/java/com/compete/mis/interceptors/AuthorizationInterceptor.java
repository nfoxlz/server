package com.compete.mis.interceptors;

import com.compete.mis.runtime.Session;
import com.compete.mis.services.TenantService;
import com.compete.mis.util.CommonUtils;
import com.compete.mis.util.ReferenceValue;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.servlet.HandlerInterceptor;

public final class AuthorizationInterceptor implements HandlerInterceptor {

    @DubboReference
    private TenantService service;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();    // 取得Cookies。
        if (null == cookies || 0 == cookies.length)
            return false;

        ReferenceValue<Long> tenantId = new ReferenceValue<Long>(), userId = new ReferenceValue<Long>();
        if (CommonUtils.loadCookie(request, tenantId, userId)) {
            Session.setTenant(service.getTenant(tenantId.get()));
            Session.setUser(userId.get());
            return true;
        } else
            return false;
    }
}
