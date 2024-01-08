package com.compete.mis.controllers;

import com.compete.mis.models.viewmodels.*;
import com.compete.mis.services.DataService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/Data")
public class DataController {

    @DubboReference
    private DataService service;

    @RequestMapping(value = "/QueryTable", method = RequestMethod.POST, produces = "application/json")
    public SimpleDataTable queryTable(final @RequestBody QueryParameter parameter) throws IOException {
        return service.queryTable(parameter.getPath(), parameter.getName(), parameter.getParameters());
    }

    @RequestMapping(value = "/Query", method = RequestMethod.POST, produces = "application/json")
    public List<SimpleDataTable> query(final @RequestBody QueryParameter parameter) throws IOException {
        return service.query(parameter.getPath(), parameter.getName(), parameter.getParameters());
    }

    @RequestMapping(value = "/PagingQuery", method = RequestMethod.POST, produces = "application/json")
    public PagingQueryResult pagingQuery(final @RequestBody PagingQueryParameter parameter) throws IOException {
        return service.pagingQuery(parameter.getPath(), parameter.getName(), parameter.getParameters(), parameter.getCurrentPageNo(), parameter.getPageSize());
    }

    @RequestMapping(value = "/Save", method = RequestMethod.POST, produces = "application/json")
    public Result save(final @RequestBody SaveParameter parameter) throws IOException {
        return service.save(parameter.getPath(), parameter.getName(), parameter.getData(), parameter.getActionId());
    }

    @RequestMapping(value = "/DifferentiatedSave", method = RequestMethod.POST, produces = "application/json")
    public Result differentiatedSave(final @RequestBody DifferentiatedSaveParameter parameter) throws IOException {
        return service.differentiatedSave(parameter.getPath(), parameter.getName(), parameter.getData(), parameter.getActionId());
    }
}
