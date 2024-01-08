package com.compete.mis.repositories;

import com.compete.mis.runtime.Session;

import javax.sql.DataSource;
import java.io.IOException;

public interface DataSourceBuilder {
    /**
     * 取得默认数据源。
     * @return 默认数据源。
     */
    default DataSource getDataSource() {
        return getDataSource(Session.getTenant().getDbServerName());
    }

    default DataSource getReadOnlyDataSource() {
        return getDataSource(Session.getTenant().getReadOnlyDbServerName());
    }

    /**
     * 取得指定名称的数据源。
     * @param name 数据源名称。即“src/main/resources/database.{name}.properties”中的{name}。
     * @return 数据源。
     */
    DataSource getDataSource(String name);

    /**
     * 取得数据库管理系统（DBMS）名称。
     * @param name 数据源名称。
     * @return 数据库管理系统（DBMS）名称。
     */
    String getDbmsName(String name);

    default String getDbmsName() throws IOException {
        return getDbmsName(Session.getTenant().getDbServerName());
    }

    default String getReadOnlyDbmsName() throws IOException {
        return getDbmsName(Session.getTenant().getReadOnlyDbServerName());
    }
}
