package com.compete.mis.controllers;

import com.compete.mis.models.User;
import com.compete.mis.models.viewmodels.LoginViewModel;
import com.compete.mis.services.AccountService;
import com.compete.mis.util.CommonUtils;
import com.compete.mis.util.Global;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/Account")
public class AccountController {

//    @DubboReference(filter = "ConsumerAuthorizationFilter")
    @DubboReference
    private AccountService service;

    /**
     * 验证并获用户信息。
     * @param model 视图模型（租户、用户、口令）。
     * @param response 响应，用于保存Cookie。
     * @return 用户信息。
     */
    @RequestMapping(value = "/Authenticate", method = RequestMethod.POST, produces = "application/json")
    public User authenticateAndGetUser(@RequestBody final LoginViewModel model, HttpServletResponse response)
            throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        User result = service.authenticateAndGetUser(model);
        if (null == result)
            return null;

        if (!Global.verify(model.getPassword(), result.getUserPassword()))
            return null;

        result.setUserPassword("*");
//        Tenant tenant = new Tenant();
//        tenant.setId(result.getTenant().getId());
//        tenant.setCode(result.getTenant().getCode());
//        tenant.setName(result.getTenant().getName());
//        result.setTenant(tenant);
        result.getTenant().setDbServerName("");
        result.getTenant().setReadOnlyDbServerName("");

        CommonUtils.saveCookie(response, result.getTenant().getId(), result.getId());
        return result;
    }
}
