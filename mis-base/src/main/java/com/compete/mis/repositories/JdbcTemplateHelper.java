package com.compete.mis.repositories;

import com.alibaba.fastjson2.JSON;
import com.compete.mis.models.data.DataColumn;
import com.compete.mis.models.data.DataTable;
import com.compete.mis.models.viewmodels.SequenceInfo;
import com.compete.mis.models.viewmodels.SimpleDataTable;
import com.compete.mis.runtime.Session;
import com.compete.mis.util.Global;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.ResourceUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public final class JdbcTemplateHelper {

    private final class ObjectFieldData {

        private int columnIndex;

        private int dataType;

        private Method method;

        public int getColumnIndex() {
            return columnIndex;
        }

        public void setColumnIndex(int columnIndex) {
            this.columnIndex = columnIndex;
        }

        public int getDataType() {
            return dataType;
        }

        public void setDataType(int dataType) {
            this.dataType = dataType;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }
    }

    private static Cache<DataSource, NamedParameterJdbcTemplate> jdbcTemplateCache = Caffeine
            .newBuilder()
            .maximumSize(128)
            .expireAfterWrite(24, TimeUnit.HOURS)
            .build(key -> {
                return new NamedParameterJdbcTemplate(key);
            });

    private static NamedParameterJdbcTemplate getJdbcTemplate(DataSource source) {
        return jdbcTemplateCache.get(source, key -> {
            return new NamedParameterJdbcTemplate(key);
        });
    }

    private static Map<Long, SequenceInfo> sequenceInfoMap = null;

    private static final Object sequenceInfoLock = new Object();

    /**
     * 数据源构建器。
     */
    @Autowired
    private DataSourceBuilder builder;

    @Autowired
    private SqlHelper helper;

//    private NamedParameterJdbcTemplate jdbcTemplate = null;
//
//    private final Object jdbcTemplateLock = new Object();
//
//    private NamedParameterJdbcTemplate readOnlyJdbcTemplate = null;
//
//    private final Object readOnlyJdbcTemplateLock = new Object();

//    public JdbcTemplateHelper(DataSourceBuilder builder) {
//        this.builder = builder;
//
//        jdbcTemplate = getJdbcTemplate(builder.getDataSource());
//        readOnlyJdbcTemplate = getJdbcTemplate(builder.getReadOnlyDataSource());
//    }

//    private NamedParameterJdbcTemplate getJdbcTemplate() {
////        if (null == jdbcTemplate)
////            synchronized (jdbcTemplateLock) {
////                if (null == jdbcTemplate)
////                    jdbcTemplate = getJdbcTemplate(builder.getDataSource());
////            }
////        return jdbcTemplate;
//        return getJdbcTemplate(builder.getDataSource());
//    }
//
//    private NamedParameterJdbcTemplate getReadOnlyJdbcTemplate() {
////        if (null == readOnlyJdbcTemplate)
////            synchronized (readOnlyJdbcTemplateLock) {
////                if (null == readOnlyJdbcTemplate)
////                    readOnlyJdbcTemplate = getJdbcTemplate(builder.getReadOnlyDataSource());
////            }
////        return readOnlyJdbcTemplate;
//        return getJdbcTemplate(builder.getReadOnlyDataSource());
//    }

    public <T> T query(final String sql, final Map<String, ?> paramMap, final Class<T> requiredType) {
        try {
            return getJdbcTemplate(builder.getReadOnlyDataSource())
                    .queryForObject(sql, paramMap, requiredType);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public <T> T query(final String path, final String name, final Map<String, ?> paramMap, final Class<T> requiredType)
            throws IOException {
        Map<String, ?> parameters = addConvertParameters(paramMap, path, name);
        return query(helper.getReadOnlySql(path, name, parameters), parameters, requiredType);
    }

    public <T> T queryForUpdate(final String sql, final Map<String, ?> paramMap, ResultSetExtractor<T> extractor) {
        try {
            return getJdbcTemplate(builder.getDataSource())
                    .query(sql, paramMap, extractor);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public <T> T queryForUpdate(final String sql, final Map<String, ?> paramMap, final Class<T> requiredType) {
        try {
            return getJdbcTemplate(builder.getDataSource()).queryForObject(sql, paramMap, requiredType);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public <T> T queryForUpdate(final String path, final String name, final Map<String, ?> paramMap, final Class<T> requiredType)
            throws IOException {
        Map<String, ?> parameters = addConvertParameters(paramMap, path, name);
        return queryForUpdate(helper.getSql(path, name, parameters), parameters, requiredType);
    }

//    public SqlRowSet query(final String sql, final Map<String, ?> paramMap) {
//        try {
//            return getJdbcTemplate(builder.getReadOnlyDataSource())
//                    .queryForRowSet(sql, paramMap);
//        } catch (EmptyResultDataAccessException e) {
//            return null;
//        }
//    }

    public <T> T query(final String sql, final Map<String, ?> paramMap, ResultSetExtractor<T> extractor) {
        try {
            return getJdbcTemplate(builder.getReadOnlyDataSource())
                    .query(sql, paramMap, extractor);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public <T> T query(final String path, final String name, final Map<String, ?> paramMap, ResultSetExtractor<T> extractor) throws IOException {
        Map<String, ?> parameters = addConvertParameters(paramMap, path, name);
        return query(helper.getReadOnlySql(path, name, parameters), parameters, extractor);
    }

    public <T> List<T> query(final String sql, final Map<String, ?> paramMap, final RowMapper<T> rowMapper) {
        try {
            return getJdbcTemplate(builder.getReadOnlyDataSource())
                    .query(sql, paramMap, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public <T> List<T> query(final String path, final String name, final Map<String, ?> paramMap, RowMapper<T> rowMapper)
            throws IOException {
        Map<String, ?> parameters = addConvertParameters(paramMap, path, name);
        return query(helper.getReadOnlySql(path, name, parameters), parameters, rowMapper);
    }

    public <T> List<T> queryForObjectList(final String sql, final Map<String, ?> paramMap, final Class<T> elementType) {
//        Field[] fields = elementType.getDeclaredFields();
        Method[] methods = elementType.getMethods();
//        AtomicReference<ResultSetMetaData> resultSetMetaData = new AtomicReference<>();
        List<ObjectFieldData> fieldList = new ArrayList<>();

        return query(sql, paramMap, (resultSet, rowIndex) -> {
            try {
                T result = elementType.getDeclaredConstructor().newInstance();

                if (0 == rowIndex) {
//                    resultSetMetaData.set(resultSet.getMetaData());
                    String fieldName;
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int count = metaData.getColumnCount();
                    for (int index = 1; index <= count; index++) {
                        fieldName = String
                                .format("set%s", metaData.getColumnName(index).replace("_", ""))
                                .toLowerCase();
                        for (Method method : methods)
                            if (fieldName.equals(method.getName().toLowerCase())) {
                                ObjectFieldData data = new ObjectFieldData();
                                data.setColumnIndex(index);
                                data.setDataType(metaData.getColumnType(index));
                                data.setMethod(method);
                                fieldList.add(data);
                                break;
                            }
                    }
                }

                // 设置列信息。
                for (ObjectFieldData fieldData : fieldList)
                    fieldData.getMethod().invoke(result, getValue(resultSet, fieldData.getColumnIndex(), fieldData.getDataType()));

//                int count = resultSetMetaData.get().getColumnCount();
//                // columns.clear();
//                for (int index = 1; index <= count; index++) {
//                    String columnName = resultSetMetaData.get().getColumnName(index);
//                    for (Field field : fields)
//                        if (field.getName().toLowerCase() == columnName.toLowerCase()) {
//                            field.set(result, getValue(resultSet, index, resultSetMetaData.get().getColumnType(index)));
//                            break;
//                        }
//                }
                return result;
            } catch (InstantiationException
                     | IllegalAccessException
                     | InvocationTargetException
                     | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public <T> List<T> queryForObjectList(final String path, final String name, final Map<String, ?> paramMap, final Class<T> elementType)
            throws IOException {
        Map<String, ?> parameters = addConvertParameters(paramMap, path, name);
        return queryForObjectList(helper.getReadOnlySql(path, name, parameters), parameters, elementType);
    }

    public <T> T queryForObject(final String sql, final Map<String, ?> paramMap, final Class<T> elementType) {
        List<T> list = queryForObjectList(sql, paramMap, elementType);
        return list.size() > 0 ? list.get(0) : null;
    }

    public <T> T queryForObject(final String path, final String name, final Map<String, ?> paramMap, final Class<T> elementType)
            throws IOException {
        Map<String, ?> parameters = addConvertParameters(paramMap, path, name);
        return queryForObject(helper.getReadOnlySql(path, name, parameters), parameters, elementType);
    }

    public <T> List<T> queryForList(final String sql, final Map<String, ?> paramMap, final Class<T> elementType) {
        return getJdbcTemplate(builder.getReadOnlyDataSource())
                .queryForList(sql, paramMap, elementType);
    }

    public <T> List<T> queryForList(final String path, final String name, final Map<String, ?> paramMap, final Class<T> elementType)
            throws IOException {
        Map<String, ?> parameters = addConvertParameters(paramMap, path, name);
        return queryForList(helper.getReadOnlySql(path, name, parameters), parameters, elementType);
    }

    private static final LobHandler LOB_HANDLER = new DefaultLobHandler();

//    private static Object getValue(final SqlRowSet rowSet, final int columnIndex, final int dataType) {
//        switch (dataType) {
//            case Types.NVARCHAR:
//            case Types.LONGNVARCHAR:
//            case Types.NCHAR:
//            case Types.NCLOB:
//            case Types.SQLXML:
//                return rowSet.getNString(columnIndex);
//            case Types.VARCHAR:
//            case Types.LONGVARCHAR:
//            case Types.CHAR:
//            case Types.CLOB:
//            case Types.ROWID:
//            case Types.DATALINK:
//                return rowSet.getString(columnIndex);
//            case Types.BIGINT:
//                return rowSet.getLong(columnIndex);
//            case Types.INTEGER:
//                return rowSet.getInt(columnIndex);
//            case Types.SMALLINT:
//                return rowSet.getShort(columnIndex);
//            case Types.TINYINT:
//                return rowSet.getByte(columnIndex);
//            case Types.BOOLEAN:
//            case Types.BIT:
//                return rowSet.getBoolean(columnIndex);
//            case Types.BINARY:
//            case Types.VARBINARY:
//            case Types.LONGVARBINARY:
//            case Types.BLOB:
//            case Types.ARRAY:
//                //rowSet
//                return LOB_HANDLER.getBlobAsBytes(rowSet, columnIndex);
//            case Types.TIMESTAMP:
//            case Types.TIMESTAMP_WITH_TIMEZONE:
//                return rowSet.getTimestamp(columnIndex);
//            case Types.DATE:
//                return rowSet.getDate(columnIndex);
//            case Types.TIME:
//            case Types.TIME_WITH_TIMEZONE:
//                return rowSet.getTime(columnIndex);
//            case Types.DECIMAL:
//            case Types.NUMERIC:
//                return rowSet.getBigDecimal(columnIndex);
//            case Types.DOUBLE:
//                return rowSet.getDouble(columnIndex);
//            case Types.FLOAT:
//            case Types.REAL:
//                return rowSet.getFloat(columnIndex);
//            default:
//                return null;
//        }
//    }

    private static Object getValue(final ResultSet resultSet, final int columnIndex, final int dataType)
            throws SQLException {

        switch (dataType) {
            case Types.NVARCHAR:
            case Types.LONGNVARCHAR:
            case Types.NCHAR:
            case Types.NCLOB:
            case Types.SQLXML:
                return resultSet.getNString(columnIndex);
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
            case Types.CHAR:
            case Types.CLOB:
            case Types.ROWID:
            case Types.DATALINK:
                return resultSet.getString(columnIndex);
            case Types.BIGINT:
                return resultSet.getLong(columnIndex);
            case Types.INTEGER:
                return resultSet.getInt(columnIndex);
            case Types.SMALLINT:
                return resultSet.getShort(columnIndex);
            case Types.TINYINT:
                return resultSet.getByte(columnIndex);
            case Types.BOOLEAN:
            case Types.BIT:
                return resultSet.getBoolean(columnIndex);
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
            case Types.BLOB:
            case Types.ARRAY:
                return LOB_HANDLER.getBlobAsBytes(resultSet, columnIndex);
            case Types.TIMESTAMP:
            case Types.TIMESTAMP_WITH_TIMEZONE:
                return resultSet.getTimestamp(columnIndex);
            case Types.DATE:
                return resultSet.getDate(columnIndex);
            case Types.TIME:
            case Types.TIME_WITH_TIMEZONE:
                return resultSet.getTime(columnIndex);
            case Types.DECIMAL:
            case Types.NUMERIC:
                return resultSet.getBigDecimal(columnIndex);
            case Types.DOUBLE:
                return resultSet.getDouble(columnIndex);
            case Types.FLOAT:
            case Types.REAL:
                return resultSet.getFloat(columnIndex);
            default:
                return null;
        }
    }

    public DataTable queryForTable(final String sql, final Map<String, ?> paramMap, final String tableName, final boolean isUpdate) {

//        final DataTable table = new DataTable();
//        table.setTableName(tableName);
//        final List<DataColumn> columns = new ArrayList<>();
//        table.setColumns(columns);
//        //final LobHandler handler = new DefaultLobHandler();
        ResultSetExtractor<DataTable> extractor = (resultSet) -> {

            final DataTable table = new DataTable();
            table.setTableName(tableName);

            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            // 设置列信息。
            int count = resultSetMetaData.getColumnCount();
            final List<DataColumn> columns = new ArrayList<>();
            // columns.clear();
            for (int index = 1; index <= count; index++) {
                DataColumn column = new DataColumn();
                column.setColumnName(resultSetMetaData.getColumnName(index));
                column.setDataType(resultSetMetaData.getColumnType(index));
                columns.add(column);
            }
            table.setColumns(columns);

            List<List<?>> rows = new ArrayList<>();
            List<Object> row;
            while (resultSet.next()) {
                row = new ArrayList<>();
                for (int index = 0; index < count; index++)
                    row.add(getValue(resultSet, index + 1, columns.get(index).getDataType()));
                rows.add(row);
            }
            table.setRows(rows);

            return table;
        };

        return isUpdate ? queryForUpdate(sql, paramMap, extractor) : query(sql, paramMap, extractor);

//        table.setRows(query(sql, paramMap, (resultSet, rowIndex) -> {
//
//            if (0 == rowIndex) {
//                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
//
//                // 设置列信息。
//                int count = resultSetMetaData.getColumnCount();
//                // columns.clear();
//                for (int index = 1; index <= count; index++) {
//                    DataColumn column = new DataColumn();
//                    column.setColumnName(resultSetMetaData.getColumnName(index));
//                    column.setDataType(resultSetMetaData.getColumnType(index));
//                    columns.add(column);
//                }
//            }
//
//            int count = columns.size();
//            List<Object> row = new ArrayList<>();
//            for (int index = 0; index < count; index++)
//                row.add(getValue(resultSet, index + 1, columns.get(index).getDataType()));
//
//            return row;
//        }));

//        return table;
    }

    public DataTable queryForTable(final String path, final String name, final Map<String, ?> paramMap, final String tableName, final boolean isUpdate)
            throws IOException {
        Map<String, ?> parameters = addConvertParameters(paramMap, path, name);
        return queryForTable(isUpdate ? helper.getSql(path, name, parameters) : helper.getReadOnlySql(path, name, parameters),
                parameters, tableName, isUpdate);
    }

    public SimpleDataTable queryForSimpleTable(final String sql, final Map<String, ?> paramMap, final boolean isUpdate) {

        DataTable table = queryForTable(sql, paramMap, isUpdate);
        SimpleDataTable result = new SimpleDataTable();

        List<DataColumn> dataColumns = table.getColumns();
        int columnSize = dataColumns.size();
        String[] columns = new String[columnSize];
        for (int index = 0; index < columnSize; index++)
            columns[index] = dataColumns.get(index).getColumnName();
        result.setColumns(columns);

        List<List<?>> dataRows = table.getRows();
        int rowSize = dataRows.size();
        Object[][] rows = new Object[rowSize][columnSize];
        for (int rowIndex = 0; rowIndex < rowSize; rowIndex++)
            for (int columnIndex = 0; columnIndex < columnSize; columnIndex++)
                rows[rowIndex][columnIndex] = dataRows.get(rowIndex).get(columnIndex);
        result.setRows(rows);

        return result;
    }

    public SimpleDataTable queryForSimpleTable(final String path, final String name, final Map<String, ?> paramMap)
            throws IOException {
        return queryForSimpleTable(path, name, paramMap, false);
    }

    public SimpleDataTable queryForSimpleTable(final String path, final String name, final Map<String, ?> paramMap, final boolean isUpdate)
            throws IOException {
        Map<String, ?> parameters = addConvertParameters(paramMap, path, name);
        SimpleDataTable result = queryForSimpleTable(isUpdate ? helper.getSql(path, name, parameters) : helper.getReadOnlySql(path, name, parameters),
                parameters, isUpdate);
        result.setTableName(name);
        return result;
    }

    public List<SimpleDataTable> queryForSimpleSet(final String path, final String name, final Map<String, ?> paramMap)
            throws IOException {
        return queryForSimpleSet(path, name, paramMap, false);
    }

    @FunctionalInterface
    private interface SqlExists {
        boolean exists(String path, String name);
    }

    public List<SimpleDataTable> queryForSimpleSet(final String path, final String name, final Map<String, ?> paramMap, final boolean isUpdate)
            throws IOException {

        int index = 0;
        String fileName = name;
        SqlExists sqlExists = isUpdate ? (p, n) -> exists(p, n) : (p, n) -> existsReadOnly(p, n);
        List<SimpleDataTable> tables = new ArrayList<>();
        while (sqlExists.exists(path, fileName)) {
            tables.add(queryForSimpleTable(path, fileName, paramMap, isUpdate));
            fileName = String.format("%s_%d", name, ++index);
        }

        return tables;
    }

    private static final String defaultTableName = "Table1";

    public DataTable queryForTable(final String sql, final Map<String, ?> paramMap, final boolean isUpdate) {
        return queryForTable(sql, paramMap, defaultTableName, isUpdate);
    }

    public DataTable queryForTable(final String path, final String name, final Map<String, ?> paramMap, final boolean isUpdate)
            throws IOException {
        return queryForTable(path, name, paramMap, defaultTableName, isUpdate);
    }

    public List<DataTable> query(final String path, final String name, final Map<String, ?> paramMap, final boolean isUpdate)
            throws IOException {
        String sqlName = name;
        int index = 0;
        SqlExists sqlExists = isUpdate ? (p, n) -> exists(p, n) : (p, n) -> existsReadOnly(p, n);
        final List<DataTable> result = new ArrayList<>();
        while (sqlExists.exists(path, sqlName)) {
            result.add(queryForTable(path, sqlName, paramMap, "Table" + ++index, isUpdate));
            sqlName = name + "." + index;
        }
        return result;
    }

    public int update(final String sql, final Map<String, ?> paramMap) {
        return getJdbcTemplate(builder.getDataSource()).update(sql, paramMap);
    }

    public int update(final String path, final String name, final Map<String, ?> paramMap) throws IOException {
        Map<String, ?> parameters = addConvertParameters(paramMap, path, name);
        return update(helper.getSql(path, name, parameters), parameters);
    }

    public boolean exists(final String path, final String name) {
        try {
            return helper.exists(path, name);
        } catch (IOException e) {
            return false;
        }
    }

    public boolean existsReadOnly(final String path, final String name) {
        try {
            return helper.existsReadOnly(path, name);
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean checkUseTransaction(final String path, final String name) {
        return SqlHelper.getSqlConfig(path, name).isUseTransaction();
    }

    public Map<String, ?> addSystemParameters(final Map<String, ?> paramMap, final String path) throws IOException {
        Map<String, String> sysParameters = SqlHelper.getSystemParameters(path);
//        if (null == sysParameters)
//            return paramMap;

        String sysName;
        Map<String, Object> result = null == paramMap ? new HashMap<>() : new HashMap<>(paramMap);
        for (Map.Entry<String, String> entry : sysParameters.entrySet()) {
            sysName = entry.getValue();
            if (null == sysName)
                sysName = entry.getKey();
            switch (sysName) {
                case "tenant":
                    result.put(entry.getKey(), Session.getTenant().getId());
                    break;
                case "user":
                    result.put(entry.getKey(), Session.getUser());
                    break;
                case "uuid":
                    result.put(entry.getKey(), UUID.randomUUID());
                    break;
                case "currentTimeMillis":
                    result.put(entry.getKey(), System.currentTimeMillis());
                    break;
                case "currentDate":
                    result.put(entry.getKey(), new Date());
                    break;
//                case "serverDateTime":
//                    result.put(entry.getKey(), getServerDateTime());
//                    break;
                case "accountingDate":
                    result.put(entry.getKey(), getAccountingDate());
                    break;
                default:
                    int index = sysName.indexOf(":");
                    String name = sysName.substring(0, index).trim();
                    String val = sysName.substring(index + 1).trim();
                    switch (name) {
                        case "serialNo":
                            result.put(entry.getKey(), getSerialNo(Long.parseLong(val)));
                            break;
                        case "accountingSerialNo":
                            result.put(entry.getKey(), getSerialNo(Long.parseLong(val), getAccountingCalendar()));
                            break;
                    }
                    break;
            }
        }

        return result;
    }

    private static Map<String, ?> convertDateTime(final Map<String, ?> paramMap) {
        if (null == paramMap)
            return null;

        Map<String, Object> result = new HashMap<>(paramMap);
        String name;
        Object val;
        for (Map.Entry<String, Object> entry : result.entrySet()) {

            val = entry.getValue();

            if (val instanceof Long) {

                name = entry.getKey().toLowerCase();

                if (name.endsWith("_date_time"))
                    entry.setValue(new java.sql.Timestamp((long)val));
                else if (name.endsWith("_date") || name.endsWith("_year_month"))
                    entry.setValue(new java.sql.Date((long)val));
                else if (name.endsWith("_time"))
                    entry.setValue(new java.sql.Time((long)val));
            }
        }
        return result;
    }

    public Map<String, ?> addSystemParameters(final Map<String, ?> paramMap, final String path, final String name, final String dbmsName)
            throws IOException {
        String paramPath = String.format(SqlHelper.sysParamPath, path, dbmsName, name);
//        if (!SqlHelper.existsSysParam(paramPath)) {
//            String baseName = SqlHelper.getBaseName(name);
//            if (baseName == null)
//                return paramMap;
//            paramPath = String.format(SqlHelper.sysParamPath, path, dbmsName, baseName);
//            if (!SqlHelper.existsSysParam(paramPath))
//                return paramMap;
//        }

        return SqlHelper.existsSysParam(paramPath) ? addSystemParameters(paramMap, paramPath) : paramMap;
    }

    private Map<String, ?> addConvertParameters(final Map<String, ?> paramMap, final String path, final String name) throws IOException {
        return convertDateTime(addSystemParameters(paramMap, path, name, builder.getDbmsName()));
    }

    public Map<String, Object> getRelatedParameters(final String path, final String name, final List<SimpleDataTable> data)
            throws IOException {

        if (!helper.existsRelatedParam(path, name) || null == data)
            return null;

        Object[][] rows;
        String[] columns;
        int index, columnCount;
//        SimpleDataTable table;
        Map<String, Object> result = new HashMap<>();
        Map<String, String> relatedParam = helper.getRelatedParam(path, name);
        for (Map.Entry<String, String> entry : relatedParam.entrySet())
            for (SimpleDataTable table : data)
                if (entry.getValue().equals(table.getTableName())) {
                    rows = table.getRows();
                    if (0 < rows.length) {
                        columns = table.getColumns();
                        columnCount = columns.length;
                        for (index = 0; index < columnCount; index++)
                            if (columns[index].equals(entry.getKey())) {
                                result.put(entry.getKey(), rows[0][index]);
                                break;
                            }
                    }
                    break;
                }
//            if (data.containsKey(entry.getValue())) {
//                table = data.get(entry.getValue());
//                rows = table.getRows();
//                if (0 < rows.length) {
//                    columns = table.getColumns();
//                    columnCount = columns.length;
//                    for (index = 0; index < columnCount; index++)
//                        if (columns[index].equals(entry.getKey())) {
//                            result.put(entry.getKey(), rows[0][index]);
//                            break;
//                        }
//                }
//            }

        return result;
    }

    public static SequenceInfo getSequenceInfo(final long no) throws IOException {
        if (null == sequenceInfoMap)
            synchronized(sequenceInfoLock) {
                if (null == sequenceInfoMap) {
                    sequenceInfoMap = new HashMap<>();
                    for (SequenceInfo info : JSON.parseArray(
                            Files.readString(ResourceUtils.getFile(Global.getLocalPath("sequenceInfo.json")).toPath()),//classpath:
                            SequenceInfo.class))
                        sequenceInfoMap.put(info.getNo(), info);
                }
            }

        return sequenceInfoMap.containsKey(no) ? sequenceInfoMap.get(no) : null;
    }

    private static int ToTenDays(final int days) {
        int result = days / 10;
        return result == 3 ? 2 : result;
    }

    private Calendar getAccountingCalendar() throws IOException {
        Integer number = query(systemFramePath, "getAccountingDate", null, Integer.class);
        int year = number / 10000;
        int month = number / 100 - year * 100;
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, number - year * 10000 - month * 100); // 注意月份是从0开始计数的，所以5月应写为Calendar.MAY
        return calendar;
    }

    public Date getAccountingDate() throws IOException {
        return getAccountingCalendar().getTime();
    }

    public Timestamp getServerDateTime() throws IOException {
        return query("system/frame", "getServerDateTime", null, Timestamp.class);
    }

    private static final String systemFramePath = "system/frame";

    public String getSerialNo(final long no) throws IOException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getServerDateTime());
        return getSerialNo(no, calendar);
    }

    public String getSerialNo(final long no, final Calendar calendar) throws IOException {
        //helper.queryForObject("system/frame", "getSerialNo", new HashMap<>(){{"no", no}}, long.class);
        //String studentStr = JSON.toJSONString(new Object());
        //JSONObject jsonObject = new JSONObject();

        SequenceInfo sequenceInfo = getSequenceInfo(no);
        if (null == sequenceInfo)
            return null;

//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(query("system/frame", "getServerDateTime", null, Timestamp.class));

        int period = switch (sequenceInfo.getPeriodType()) {
            case None -> 0;
            case Year -> calendar.get(Calendar.YEAR);
            case Quarter -> calendar.get(Calendar.YEAR) * 10 + calendar.get(Calendar.MONTH) / 3;
            case Month -> calendar.get(Calendar.YEAR) * 100 + calendar.get(Calendar.MONTH) + 1;
            case TenDays -> calendar.get(Calendar.YEAR) * 1000
                    + (calendar.get(Calendar.MONTH) + 1) * 10 + ToTenDays(calendar.get(Calendar.DAY_OF_MONTH));
            case Week -> calendar.get(Calendar.YEAR) * 100 + calendar.get(Calendar.WEEK_OF_YEAR);
            case Day -> calendar.get(Calendar.YEAR) * 1000 + calendar.get(Calendar.DAY_OF_YEAR);
            case Hour -> calendar.get(Calendar.YEAR) * 100000
                    + calendar.get(Calendar.DAY_OF_YEAR) * 100 + calendar.get(Calendar.HOUR);
            case QuarterHour -> calendar.get(Calendar.YEAR) * 1000000 + calendar.get(Calendar.DAY_OF_YEAR) * 1000
                    + calendar.get(Calendar.HOUR) * 10 + calendar.get(Calendar.MINUTE) / 15;
            case Minute -> calendar.get(Calendar.YEAR) * 1000000 + (calendar.get(Calendar.DAY_OF_YEAR) - 1) * 1440
                    + calendar.get(Calendar.HOUR) * 60 + calendar.get(Calendar.MINUTE);
        };

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("no", no);
        paramMap.put("period", period);
        Long number = queryForUpdate(systemFramePath, "getSerialNo", paramMap, Long.class);
        if (null == number)
            number = 1L;

        update(systemFramePath, "insertSerialNo", paramMap);

        return null == sequenceInfo.getFormat() ? String.valueOf(number)
                : String.format(sequenceInfo.getFormat(), calendar, number);
    }

    public <T> T execute(TransactionCallback<T> action) {
        return new TransactionTemplate(new JdbcTransactionManager(builder.getDataSource())).execute(action);
    }
}
