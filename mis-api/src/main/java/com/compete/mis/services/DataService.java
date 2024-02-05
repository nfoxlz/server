package com.compete.mis.services;

import com.compete.mis.models.viewmodels.PagingQueryResult;
import com.compete.mis.models.viewmodels.Result;
import com.compete.mis.models.viewmodels.SaveData;
import com.compete.mis.models.viewmodels.SimpleDataTable;

import java.io.IOException;
import java.util.Map;

public interface DataService {

    SimpleDataTable queryTable(final String path, final String name, final Map<String, ?> paramMap) throws IOException;

    Map<String, SimpleDataTable> query(final String path, final String name, final Map<String, ?> paramMap) throws IOException;

    PagingQueryResult pagingQuery(final String path, final String name, final Map<String, ?> paramMap, long currentPageNo, short pageSize) throws IOException;

    Result save(final String path, final String name, final Map<String, SimpleDataTable> data, final byte[] actionId);

    Result differentiatedSave(final String path, final String name, final Map<String, SaveData> data, final byte[] actionId);

    Result verify(final String path, final String name, final Map<String, SimpleDataTable> data) throws IOException;
}
