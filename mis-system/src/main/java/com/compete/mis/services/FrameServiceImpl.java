package com.compete.mis.services;

import com.compete.mis.models.viewmodels.EnumInfo;
import com.compete.mis.models.viewmodels.Menu;
import com.compete.mis.repositories.HikariDataSourceBuilder;
import com.compete.mis.repositories.JdbcTemplateHelper;
import com.compete.mis.repositories.SqlHelper;
import com.compete.mis.runtime.Session;
import com.compete.mis.util.ErrorManager;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DubboService
public class FrameServiceImpl implements FrameService {

    @Autowired
    private JdbcTemplateHelper helper;

    /**
     * @return
     */
    @Override
    public List<Menu> getMenus() throws IOException {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("application", 0L);
        paramMap.put("client_Side", 0L);
        paramMap.put("user_Id", Session.getUser());
        return helper.queryForObjectList("system/frame", "getMenus", paramMap, Menu.class);
    }

    /**
     * @return
     */
    @Override
    public List<EnumInfo> getEnums() throws IOException {
        return helper.queryForObjectList("system/frame", "getEnums", null, EnumInfo.class);
    }

    /**
     * @return
     */
    @Override
    public Timestamp getServerDateTime() throws IOException {
        return helper.query("system/frame", "getServerDateTime", null, Timestamp.class);
    }

    /**
     * @param no
     * @return
     */
    @Override
    public String getSerialNo(long no) throws IOException {
        return helper.getSerialNo(no);
    }

    /**
     *
     */
    @Override
    public void clearCache() {
        SqlHelper.clearCache();
        HikariDataSourceBuilder.clearCache();
        ErrorManager.clearCache();
    }
}
