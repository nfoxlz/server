package com.compete.mis.controllers;

import com.compete.mis.models.viewmodels.EnumInfo;
import com.compete.mis.models.viewmodels.Menu;
import com.compete.mis.services.FrameService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

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

    @RequestMapping(value = "/GetServerDateTime", method = RequestMethod.POST, produces = "application/json")
    public Timestamp getServerDateTime() throws IOException {
        return service.getServerDateTime();
    }

    @RequestMapping(value = "/ClearCache", method = RequestMethod.POST, produces = "application/json")
    public void clearCache() {
        service.clearCache();
    }
}
