package com.compete.mis.services;

import com.compete.mis.models.viewmodels.EnumInfo;
import com.compete.mis.models.viewmodels.Menu;
import com.compete.mis.models.viewmodels.PeriodYearMonthParameter;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface FrameService {
    List<Menu> getMenus() throws IOException;

    List<EnumInfo> getEnums() throws IOException;

    Map<String, String> getSettings() throws IOException;

    Timestamp getServerDateTime() throws IOException;

    Date getAccountingDate() throws IOException;

    boolean isFinanceClosed() throws IOException;

    boolean isFinanceClosed(int periodYearMonth) throws IOException;

    String getSerialNo(final long no) throws IOException;

    void clearCache();
}
