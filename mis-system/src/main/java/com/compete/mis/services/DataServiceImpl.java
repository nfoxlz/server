package com.compete.mis.services;

import com.compete.mis.models.viewmodels.PagingQueryResult;
import com.compete.mis.models.viewmodels.Result;
import com.compete.mis.models.viewmodels.SaveData;
import com.compete.mis.models.viewmodels.SimpleDataTable;
import com.compete.mis.repositories.JdbcTemplateHelper;
import com.compete.mis.util.ErrorManager;
import com.compete.mis.util.Global;
import jakarta.validation.constraints.NotNull;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import java.io.IOException;
import java.util.HashMap;
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
     */
    @Override
    public Map<String, SimpleDataTable> query(final String path, final String name, final Map<String, ?> paramMap) throws IOException {
        return helper.queryForSimpleSet(path, name, paramMap);
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
            beginNo = pageNo * pageSize;
            param.put("begin_No", beginNo);
            param.put("page_Size", count - beginNo);

            result.setPageNo(pageNo);
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
    public Result save(final String path, final String name, final Map<String, SimpleDataTable> data,
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
                    int count = 0, rowLength, sqlResult;
                    SimpleDataTable table;//, firstTable = null;

                    Result result = verify(path, name, data);
                    if (0 != result.getErrorNo()) {
                        status.setRollbackOnly();
                        return result;
                    }

                    long sqlIndex;
                    for (Map.Entry<String, SimpleDataTable> entry : data.entrySet()) {
                        table = entry.getValue();
//                        if (null == firstTable)
//                            firstTable = table;

                        rowLength = table.getRows().length;
                        sqlName = String.format("%s_%s", name, entry.getKey());
                        Map<String, Object> relatedParam = helper.getRelatedParameters(path, sqlName, data);
                        for (int index = 0; index < rowLength; index++) {
                            sqlIndex = 0L;
                            sqlSubname = sqlName;
                            Map<String, ?> paramMap = Global.merge(getParamMap(table, index), relatedParam);
                            while (helper.exists(path, sqlSubname)) {
                                sqlResult = helper.update(path, sqlSubname, paramMap);
                                if (0 >= sqlResult) {
//                                    throw new RuntimeException();
                                    status.setRollbackOnly();
                                    result.setErrorNo(-1);
                                    result.setMessage("并发冲突，数据没有保存，请稍后再试。");
                                    return result;
                                }
                                count += sqlResult;
                                sqlSubname = String.format("%s_%s.%s", name, entry.getKey(), ++sqlIndex);
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
        final int rowLength = table.getRows().length;
        for (int index = 0; index < rowLength; index++)
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
                int count = 0, rowLength, originalRowLength;
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
                            Result result = verify(path, "%s.%s".formatted(sqlName, "verify"), table, null);
                            if (null != result && 0 != result.getErrorNo()) {
                                status.setRollbackOnly();
                                return result;
                            }
                            count += saveTableData(path, "%s.%s".formatted(sqlName, "add"), table);
                        }

                        table = tableData.getDeletedTable();
                        if (null != table)
                            count += saveTableData(path, "%s.%s".formatted(sqlName, "delete"), table);

                        modifiedTable = tableData.getModifiedTable();
                        table = tableData.getModifiedOriginalTable();
                        if (null != modifiedTable && null != table) {
                            rowLength = modifiedTable.getRows().length;
                            originalRowLength = table.getRows().length;
                            sqlName = "%s.%s".formatted(sqlName, "modify");
                            for (int index = 0; index < rowLength && index < originalRowLength; index++) {
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

    private Result verify(final String path, final String sqlName, final @NotNull SimpleDataTable table, final Map<String, SimpleDataTable> data)
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
    public Result verify(final String path, final String name, final @NotNull Map<String, SimpleDataTable> data) throws IOException {

        String sqlName;
        Result result = null;
        Map<String, Object> param;
        for (Map.Entry<String, SimpleDataTable> entry : data.entrySet()) {
            sqlName = "%s_%s.verify".formatted(name, entry.getKey());
            result = verify(path, sqlName, entry.getValue(), data);
            if (null == result)
                continue;
            if (0 != result.getErrorNo())
                return result;
        }

        return null == result ? new Result() : result;
    }
}
