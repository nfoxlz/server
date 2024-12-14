package com.compete.mis.controllers;

import com.compete.mis.models.viewmodels.EnumInfo;
import com.compete.mis.models.viewmodels.Menu;
import com.compete.mis.models.viewmodels.ModifyPasswordParameter;
import com.compete.mis.models.viewmodels.PeriodYearMonthParameter;
import com.compete.mis.services.FrameService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/Frame")
public class FrameController {

    @DubboReference
    private FrameService service;

    @RequestMapping(value = "/GetMenus", method = RequestMethod.POST, produces = "application/json")
    public List<Menu> getMenus() throws IOException {
        return service.getMenus();
    }

    @RequestMapping(value = "/GetEnums", method = RequestMethod.POST, produces = "application/json")
    public List<EnumInfo> getEnums() throws IOException {
        return service.getEnums();
    }

    @RequestMapping(value = "/GetSettings", method = RequestMethod.POST, produces = "application/json")
    public Map<String, String> getSettings() throws IOException {
        return service.getSettings();
    }

    @RequestMapping(value = "/GetServerDateTime", method = RequestMethod.POST, produces = "application/json")
    public Timestamp getServerDateTime() throws IOException {
        return service.getServerDateTime();
    }

    @RequestMapping(value = "/GetAccountingDate", method = RequestMethod.POST, produces = "application/json")
    public Date getAccountingDate() throws IOException {
        return service.getAccountingDate();
    }

    @RequestMapping(value = "/ModifyPassword", method = RequestMethod.POST, produces = "application/json")
    public boolean modifyPassword(@RequestBody final ModifyPasswordParameter parameter)
            throws IOException, NoSuchAlgorithmException {
        return service.modifyPassword(parameter.getOriginalPassword(), parameter.getNewPassword());
    }

    @RequestMapping(value = "/IsFinanceClosed", method = RequestMethod.POST, produces = "application/json")
    public boolean isFinanceClosed() throws IOException {
        return service.isFinanceClosed();
    }

    @RequestMapping(value = "/IsFinanceClosedByDate", method = RequestMethod.POST, produces = "application/json")
    public boolean isFinanceClosedByDate(@RequestBody final PeriodYearMonthParameter parameter) throws IOException {
        return service.isFinanceClosed(parameter.getPeriodYearMonth());
    }

    @RequestMapping(value = "/ClearCache", method = RequestMethod.POST, produces = "application/json")
    public void clearCache() {
        service.clearCache();
    }
}
