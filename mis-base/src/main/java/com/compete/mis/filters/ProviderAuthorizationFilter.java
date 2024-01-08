package com.compete.mis.filters;

import com.compete.mis.models.Tenant;
import com.compete.mis.runtime.Session;
import com.compete.mis.util.Constants;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;

import java.util.regex.Pattern;

@Activate(group = CommonConstants.PROVIDER)
public final class ProviderAuthorizationFilter implements Filter {

    private static final Pattern[] ignoredPattern = new Pattern[] {
            Pattern.compile("com.compete.mis.services.AccountService:*"),
            Pattern.compile("com.compete.mis.services.TenantService:*"),
    };

    /**
     * @param invoker
     * @param invocation
     * @return
     * @throws RpcException
     */
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        String serviceName = invocation.getTargetServiceUniqueName();
        for (Pattern pattern : ignoredPattern)
            if (pattern.matcher(serviceName).find())
                return invoker.invoke(invocation);

        Object tenant = invocation.getObjectAttachment(Constants.SESSION_TENANT_NAME);
        if (null == tenant)
            return null;
        Session.setTenant((Tenant)tenant);

        Object userId = invocation.getObjectAttachment(Constants.SESSION_USER_NAME);
        if (null == userId)
            return null;
        Session.setUser((long)userId);

        return invoker.invoke(invocation);
    }
}
