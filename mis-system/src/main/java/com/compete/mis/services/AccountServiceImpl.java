package com.compete.mis.services;

import com.compete.mis.models.Tenant;
import com.compete.mis.models.User;
import com.compete.mis.models.viewmodels.LoginViewModel;
import com.compete.mis.repositories.TenantRepository;
import com.compete.mis.util.Global;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

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
    public User authenticateAndGetUser(final LoginViewModel model) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        Tenant tenant = repository.getTenantByCode(model.getTenant());
        if (null == tenant)
            return null;

        User result = repository.getUser(tenant, model.getUser());

//        if (Global.verify(new String(Global.decrypt(Base64.getDecoder().decode(model.getPassword())), StandardCharsets.UTF_8), result.getUserPassword())) {
        if (Global.verify(new String(Global.symmetricDecrypt(Base64.getDecoder().decode(model.getPassword())), StandardCharsets.UTF_8), result.getUserPassword())) {
            result.setUserPassword("*");
            return result;
        }

        return null;
    }
}
