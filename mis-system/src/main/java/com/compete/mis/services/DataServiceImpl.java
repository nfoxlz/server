package com.compete.mis.services;

import com.compete.mis.exceptions.BusinessException;
import com.compete.mis.models.viewmodels.*;
import com.compete.mis.repositories.JdbcTemplateHelper;
import com.compete.mis.util.ErrorManager;
import com.compete.mis.util.Global;
import jakarta.validation.constraints.NotNull;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionCallback;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DubboService
public class DataServiceImpl implements DataService {

    @Autowired
    private JdbcTemplateHelper helper;

    /**
     * @param path
     * @param name
     * @param paramMap
     * @return
     * @throws IOException
     */
    @Override
    public SimpleDataTable queryTable(String path, String name, Map<String, ?> paramMap) throws IOException {
        return helper.queryForSimpleTable(path, name, paramMap);
    }

    /**
     * @param path
     * @param name
     * @param paramMap
     * @return
     * @throws IOException
     */
    @Override
    public SimpleDataTable queryTableForUpdate(String path, String name, Map<String, ?> paramMap) throws IOException {
        return helper.execute(new TransactionCallback<>() {

            /**
             * @param status
             * @return
             */
            @Override
            public SimpleDataTable doInTransaction(TransactionStatus status) {
                try {
                    return helper.queryForSimpleTable(path, name, paramMap, true);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * @param path
     * @param name
     * @param paramMap
     * @return
     * @throws IOException
     */
    @Override
    public SimpleDataTable queryTableByConfig(String path, String name, Map<String, ?> paramMap) throws IOException {
        return JdbcTemplateHelper.checkUseTransaction(path, name)
                ? queryTableForUpdate(path, name, paramMap)
                : queryTable(path, name, paramMap);
    }

    /**
     * @param path
     * @param name
     * @param paramMap
     * @return
     */
    @Override
    public QueryResult query(final String path, final String name, final Map<String, ?> paramMap) throws IOException {
        QueryResult result = new QueryResult();
        try {
            result.setData(helper.queryForSimpleSet(path, name, paramMap));
        } catch (UncategorizedSQLException e) {
            Global.extractMessage(e, result);
        }
        return result;
    }

    /**
     * @param path
     * @param name
     * @param paramMap
     * @return
     * @throws IOException
     */
    @Override
    public QueryResult queryForUpdate(String path, String name, Map<String, ?> paramMap) throws IOException {
        return helper.execute(new TransactionCallback<>() {

            /**
             * @param status
             * @return
             */
            @Override
            public QueryResult doInTransaction(TransactionStatus status) {
                QueryResult result = new QueryResult();
                try {
                    result.setData(helper.queryForSimpleSet(path, name, paramMap, true));
                } catch (UncategorizedSQLException e) {
                    status.setRollbackOnly();
                    Global.extractMessage(e, result);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return result;
            }
        });
    }

    /**
     * @param path
     * @param name
     * @param paramMap
     * @return
     * @throws IOException
     */
    @Override
    public QueryResult queryByConfig(String path, String name, Map<String, ?> paramMap) throws IOException {
        return JdbcTemplateHelper.checkUseTransaction(path, name)
                ? queryForUpdate(path, name, paramMap)
                : query(path, name, paramMap);
    }

    /**
     * @param path
     * @param name
     * @param paramMap
     * @param currentPageNo
     * @param pageSize
     * @return
     */
    @Override
    public PagingQueryResult pagingQuery(final String path, final String name, final Map<String, ?> paramMap, final long currentPageNo, final short pageSize) throws IOException {

        PagingQueryResult result = new PagingQueryResult();
        long count = helper.query(path, name + ".count", paramMap, long.class);
        long beginNo = (currentPageNo - 1L) * pageSize;

        Map<String, Object> param = null == paramMap ? new HashMap<>() : new HashMap<>(paramMap);
        if (pageSize >= count) {
            param.put("begin_No", 0L);
            param.put("page_Size", count);

            result.setPageNo(1L);
        } else  if (beginNo < count) {
            param.put("begin_No", beginNo);
            param.put("page_Size", pageSize);

            result.setPageNo(currentPageNo);
        } else {
            long pageNo = count / pageSize;
            if (count % pageSize == 0)
                pageNo--;
            beginNo = pageNo * pageSize;
            param.put("begin_No", beginNo);
            param.put("page_Size", count - beginNo);

            result.setPageNo(pageNo + 1);
        }
        result.setData(helper.queryForSimpleSet(path, name, param));
        result.setCount(count);

        return result;
    }

    private Map<String, Object> getParamMap(final SimpleDataTable table, final int index) {
        String[] columns = table.getColumns();
        Object[] row = table.getRows()[index];
        int columnLength = columns.length, rowLength = row.length;

        Map<String, Object> result = new HashMap<>();
        for (int columnIndex = 0; columnIndex < columnLength && columnIndex < rowLength; columnIndex++)
            result.put(columns[columnIndex],
                    row[columnIndex] instanceof Map<?,?> && 0 == ((Map<?,?>)row[columnIndex]).size()
                            ? null : row[columnIndex]);

        if (!result.containsKey("Sn"))
            result.put("Sn", index);

        return result;
    }

    private boolean saveActionId(final byte[] actionId) throws IOException {
        try {
            Map<String, byte[]> actionParamMap = new HashMap<>();
            actionParamMap.put("id", actionId);
            helper.update("system/frame", "insertAction", actionParamMap);
        } catch (DuplicateKeyException e) {
            return true;
        }

        return false;
    }

    /**
     * @param path
     * @param name
     * @param data
     * @return
     */
    @Override
    public Result save(final String path, final String name, final List<SimpleDataTable> data,
                       final byte[] actionId) {

        return helper.execute(new TransactionCallback<>() {
            /**
             * @param status
             * @return
             */
            @Override
            public Result doInTransaction(TransactionStatus status) {

                try {
                    if (null != actionId && saveActionId(actionId))
                        return new Result();

                    String sqlName, sqlSubname;
                    int count = 0, rowLength;
//                    SimpleDataTable table;//, firstTable = null;

                    Result result = verify(path, name, data);
                    if (0 != result.getErrorNo()) {
                        status.setRollbackOnly();
                        return result;
                    }

                    long sqlIndex;
                    for (SimpleDataTable table : data) {
//                        table = entry.getValue();
//                        if (null == firstTable)
//                            firstTable = table;

                        rowLength = table.getRows().length;
                        sqlName = String.format("%s_%s", name, table.getTableName());
                        Map<String, Object> relatedParam = helper.getRelatedParameters(path, sqlName, data);
                        for (int index = 0; index < rowLength; index++) {
                            sqlIndex = 0L;
                            sqlSubname = sqlName;
                            Map<String, ?> paramMap = Global.merge(getParamMap(table, index), relatedParam);
                            while (helper.exists(path, sqlSubname)) {
                                count += helper.update(path, sqlSubname, paramMap);
                                sqlSubname = String.format("%s_%s.%s", name, table.getTableName(), ++sqlIndex);
                            }
//                            sqlResult = helper.update(path, sqlName, getParamMap(table, index));
//                            if (0 >= sqlResult)
//                                throw new RuntimeException();
//                            count += sqlResult;
                        }
                    }

                    sqlName = String.format("%s.after", name);
                    if (helper.exists(path, sqlName))
                        count += helper.update(path, sqlName, null);
//                    if (null != firstTable && 0 < firstTable.getRows().length) {
//                        sqlName = String.format("%s.after", name);
//                        if (helper.exists(path, sqlName))
//                            count += helper.update(path, sqlName, null);
////                            count += helper.update(path, sqlName, getParamMap(firstTable, 0));
//                    }

                    if (0 == count) {
//                                    throw new RuntimeException();
                        status.setRollbackOnly();
                        result.setErrorNo(-1);
                        result.setMessage("并发冲突，数据没有保存，请稍后再试。");
                        return result;
                    }

                    result.setErrorNo(count);

                    return result;
                } catch (Exception e) {
                    status.setRollbackOnly();
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private int saveTableData(final String path, final String sqlName, final SimpleDataTable table) throws IOException {
        if (!helper.exists(path, sqlName))
            return 0;

        int count = 0;
        final int rowsLength = table.getRows().length;
        for (int index = 0; index < rowsLength; index++)
            count += helper.update(path, sqlName, getParamMap(table, index));
        return count;
    }

    /**
     * @param path
     * @param name
     * @param data
     * @param actionId
     * @return
     * @throws IOException
     */
    @Override
    public Result differentiatedSave(final String path, final String name, final Map<String, SaveData> data, final byte[] actionId) {
        return helper.execute(new TransactionCallback<>() {
            /**
             * @param status
             * @return
             */
            @Override
            public Result doInTransaction(TransactionStatus status) {

                String sqlName;
                int count = 0, rowsLength;
                SaveData tableData;
                SimpleDataTable table, modifiedTable;
                Map<String, Object> param, originalParam;

                try {
                    if (null != actionId && saveActionId(actionId))
                        return new Result();

                    for (Map.Entry<String, SaveData> entry : data.entrySet()) {
                        sqlName = "%s_%s".formatted(name, entry.getKey());
                        tableData = entry.getValue();

                        table = tableData.getAddedTable();
                        if (null != table) {
                            Result result = verify(path, "%s.verify".formatted(sqlName), table, null);
                            if (null != result && 0 != result.getErrorNo()) {
                                status.setRollbackOnly();
                                return result;
                            }
                            count += saveTableData(path, "%s.add".formatted(sqlName), table);
                        }

                        table = tableData.getDeletedTable();
                        if (null != table)
                            count += saveTableData(path, "%s.delete".formatted(sqlName), table);

                        modifiedTable = tableData.getModifiedTable();
                        table = tableData.getModifiedOriginalTable();
                        if (null != modifiedTable && null != table) {
                            rowsLength = Math.min(modifiedTable.getRows().length, table.getRows().length);
                            sqlName = "%s.modify".formatted(sqlName);
                            for (int index = 0; index < rowsLength; index++) {
                                param = getParamMap(modifiedTable, index);
                                originalParam = getParamMap(table, index);
                                for (Map.Entry<String, Object> paramEntry : originalParam.entrySet())
                                    param.put("Original_%s".formatted(paramEntry.getKey()), paramEntry.getValue());
                                count += helper.update(path, sqlName, param);
                            }
                        }
                    }

                    Result result = new Result();
                    result.setErrorNo(count);

                    return result;
                } catch (Exception e) {
                    status.setRollbackOnly();
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private Result verify(final String path, final String sqlName, final @NotNull SimpleDataTable table, final List<SimpleDataTable> data)
            throws IOException {

        String verifySqlName = sqlName;

        int fileIndex = 0;
        String message;
        int rowLength = table.getRows().length;
        while (helper.exists(path, verifySqlName)) {
            Map<String, Object> param;
            Map<String, Object> relatedParam = helper.getRelatedParameters(path, verifySqlName, data);
            Result result = new Result();
            for (int index = 0; index < rowLength; index++) {
                param = Global.merge(getParamMap(table, index), relatedParam);
                message = helper.queryForUpdate(path, verifySqlName, param, String.class);
                if (null != message) {
                    try {
                        result.setErrorNo(Integer.parseInt(message));
                        result.setMessage(ErrorManager.getMessage(path, result.getErrorNo(), param));
                    } catch (NumberFormatException e) {
                        result.setErrorNo(- fileIndex - 2);
                        result.setMessage(message);
                    }
                    return result;
                }
            }

            verifySqlName = "%s.%d".formatted(sqlName, ++fileIndex);
        }

        return null;
    }

    /**
     * @param path
     * @param name
     * @param data
     * @return
     */
    @Override
    public Result verify(final String path, final String name, final @NotNull List<SimpleDataTable> data) throws IOException {

        String sqlName;
        Result result = null;
        Map<String, Object> param;
        for (SimpleDataTable table : data) {
            sqlName = "%s_%s.verify".formatted(name, table.getTableName());
            result = verify(path, sqlName, table, data);
            if (null == result)
                continue;
            if (0 != result.getErrorNo())
                return result;
        }

        return null == result ? new Result() : result;
    }
}
