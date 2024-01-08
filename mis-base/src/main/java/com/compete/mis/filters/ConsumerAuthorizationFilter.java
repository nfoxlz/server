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

@Activate(group = CommonConstants.CONSUMER)
public class ConsumerAuthorizationFilter implements Filter {

    private static final String[] ignoredServiceNames = new String[] {
            "com.compete.mis.services.AccountService",
            "com.compete.mis.services.TenantService",
    };

    /**
     * @param invoker
     * @param invocation
     * @return
     * @throws RpcException
     */
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        String serviceName = invocation.getServiceName();
        for (String name : ignoredServiceNames)
            if (name.equals(serviceName))
                return invoker.invoke(invocation);

        Tenant tenant = Session.getTenant();
        if (null == tenant)
            return null;
        invocation.setAttachment(Constants.SESSION_TENANT_NAME, tenant);

        invocation.setAttachment(Constants.SESSION_USER_NAME, Session.getUser());

        return invoker.invoke(invocation);
    }
}
