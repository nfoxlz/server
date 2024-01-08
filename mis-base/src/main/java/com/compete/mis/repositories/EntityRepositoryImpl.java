package com.compete.mis.repositories;

import com.compete.mis.runtime.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@Repository
public class EntityRepositoryImpl implements EntityRepository {

    @Autowired
    private JdbcTemplateHelper helper;

    private String entityName;

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    private static final MessageFormat messageFormatGet = new MessageFormat("SELECT *, {0}}_Id AS Id, {0}_Code AS Code, {0}_Name AS Name FROM {0} WHERE {0}_Id = :id AND AND NOT Is_Deleted AND Is_Active");

    /**
     *
     * @param id
     * @param elementType
     * @return
     * @param <T>
     */
    @Override
    public <T> T get(final long id, Class<T> elementType) {

        Map<String, Long> paramMap = new HashMap<>();
        paramMap.put("id", id);
        return helper.queryForObject(messageFormatGet.format(entityName), paramMap, elementType);
    }

    private static final MessageFormat messageFormatGetByCode = new MessageFormat("SELECT *, {0}}_Id AS Id, {0}_Code AS Code, {0}_Name AS Name FROM {0} WHERE Tenant_Id = :tenant_Id AND {0}_Code = :code AND AND NOT Is_Deleted AND Is_Active");

    /**
     *
     * @param code
     * @param elementType
     * @return
     * @param <T>
     */
    @Override
    public <T> T get(final String code, Class<T> elementType) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("tenant_Id", Session.getTenant().getId());
        paramMap.put("code", code);
        return helper.queryForObject(messageFormatGetByCode.format(entityName), paramMap, elementType);
    }

    private static final MessageFormat messageFormatDelete = new MessageFormat("UPDATE {0} SET Is_Deleted = TRUE WHERE {0}_Id = :id AND AND NOT Is_Deleted");

    /**
     * @param id
     */
    @Override
    public int delete(long id) {
        Map<String, Long> paramMap = new HashMap<>();
        paramMap.put("id", id);
        return helper.update(messageFormatDelete.format(entityName), paramMap);
    }

    private static final MessageFormat messageFormatDeleteByCode = new MessageFormat("UPDATE {0} SET Is_Deleted = TRUE WHERE Tenant_Id = :tenant_Id AND {0}_Code = :code AND AND NOT Is_Deleted");

    /**
     * @param code
     */
    @Override
    public int delete(String code) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("tenant_Id", Session.getTenant().getId());
        paramMap.put("code", code);
        return helper.update(messageFormatDeleteByCode.format(entityName), paramMap);
    }
}
