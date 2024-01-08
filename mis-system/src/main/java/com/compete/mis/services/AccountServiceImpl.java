package com.compete.mis.services;

import com.compete.mis.models.Tenant;
import com.compete.mis.models.User;
import com.compete.mis.models.viewmodels.LoginViewModel;
import com.compete.mis.repositories.TenantRepository;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class AccountServiceImpl implements AccountService {

    @Autowired
    private TenantRepository repository;

    /**
     * 验证并取得用户。
     *
     * @param model 登录视图模型。
     * @return 成功：用户数据；失败：null。
     */
    @Override
    public User authenticateAndGetUser(final LoginViewModel model) {
        Tenant tenant = repository.getTenantByCode(model.getTenant());
        if (null == tenant)
            return null;

        User result = repository.getUser(tenant, model.getUser());

        return result;
    }
}
