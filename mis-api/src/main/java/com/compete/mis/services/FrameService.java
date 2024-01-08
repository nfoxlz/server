package com.compete.mis.services;

import com.compete.mis.models.viewmodels.EnumInfo;
import com.compete.mis.models.viewmodels.Menu;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

public interface FrameService {
    List<Menu> getMenus() throws IOException;

    List<EnumInfo> getEnums() throws IOException;

    Timestamp getServerDateTime() throws IOException;

    String getSerialNo(final long no) throws IOException;

    void clearCache();
}
