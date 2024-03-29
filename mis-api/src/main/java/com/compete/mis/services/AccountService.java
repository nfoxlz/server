package com.compete.mis.services;

import com.compete.mis.models.User;
import com.compete.mis.models.viewmodels.LoginViewModel;

public interface AccountService {
    /**
     * 验证并取得用户。
     * @param model 登录视图模型。
     * @return 成功：用户数据；失败：null。
     */
    User authenticateAndGetUser(final LoginViewModel model);
}
