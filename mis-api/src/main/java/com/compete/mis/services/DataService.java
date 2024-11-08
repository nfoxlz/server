package com.compete.mis.services;

import com.compete.mis.models.viewmodels.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface DataService {

    SimpleDataTable queryTable(final String path, final String name, final Map<String, ?> paramMap) throws IOException;

    SimpleDataTable queryTableForUpdate(final String path, final String name, final Map<String, ?> paramMap) throws IOException;

    SimpleDataTable queryTableByConfig(final String path, final String name, final Map<String, ?> paramMap) throws IOException;

    QueryResult query(final String path, final String name, final Map<String, ?> paramMap) throws IOException;

    QueryResult queryForUpdate(final String path, final String name, final Map<String, ?> paramMap) throws IOException;

    QueryResult queryByConfig(final String path, final String name, final Map<String, ?> paramMap) throws IOException;

    PagingQueryResult pagingQuery(final String path, final String name, final Map<String, ?> paramMap, long currentPageNo, short pageSize) throws IOException;

    Result save(final String path, final String name, final List<SimpleDataTable> data, final byte[] actionId);

    Result differentiatedSave(final String path, final String name, final Map<String, SaveData> data, final byte[] actionId);

    Result verify(final String path, final String name, final List<SimpleDataTable> data) throws IOException;
}
