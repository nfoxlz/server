package com.compete.mis.filters;

import com.alibaba.fastjson.JSON;
import com.compete.mis.models.Tenant;
import com.compete.mis.runtime.Session;
import com.compete.mis.util.Constants;
import com.compete.mis.util.Global;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

//        Object[] arguments = invocation.getArguments();
//        if (0 < arguments.length) {
//            Object sign = invocation.getObjectAttachment(Constants.SESSION_DATA_SIGNATURE);
//            if (null != sign) {
//                List<Object> list =  new ArrayList<>(Arrays.asList(invocation.getArguments()));
//                list.add(0, "F4AE7A53-01EB-4693-8A8A-37753D4B044B");
//                list.add(0, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
//                String json = JSON.toJSONString(list);
//
//                String signString = (String) sign;
//                if (!Global.verify(JSON.toJSONString(list), signString))
//                    return null;
//
//                Session.setSign(signString);
//            }
//        }

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
