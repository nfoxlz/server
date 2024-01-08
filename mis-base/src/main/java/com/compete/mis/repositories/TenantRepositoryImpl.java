package com.compete.mis.repositories;

import com.compete.mis.models.Tenant;
import com.compete.mis.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TenantRepositoryImpl implements TenantRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private DataSourceBuilder builder;

    @Value("${dbmsName}")
    private String tenantDbmsName;

    private <T> List<T> queryForObjectList(final String path, final String name, final Map<String, ?> paramMap, final RowMapper<T> rowMapper) {
        return jdbcTemplate.query(SqlHelper.getSql(path, name, tenantDbmsName, paramMap), paramMap, rowMapper);
    }

    private <T> T queryForObject(final String path, final String name, final Map<String, ?> paramMap, final RowMapper<T> rowMapper) {
        List<T> list = queryForObjectList(path, name, paramMap, rowMapper);
        return list.size() > 0 ? list.get(0) : null;
    }

    private Tenant createTenant(ResultSet resultSet) throws SQLException {
        Tenant tenant = new Tenant();
        tenant.setId(resultSet.getLong("Tenant_Id"));
        tenant.setCode(resultSet.getString("Tenant_Code"));
        tenant.setName(resultSet.getString("Tenant_Name"));
        tenant.setDbServerName(resultSet.getString("Db_Server_Name"));
        tenant.setReadOnlyDbServerName(resultSet.getString("Read_Only_Db_Server_Name"));
        return tenant;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Tenant getTenant(long id) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", id);

        return queryForObject("system/common", "getTenant", paramMap,
                (resultSet, rowIndex) -> createTenant(resultSet));
    }

    @Override
    public Tenant getTenantByCode(final String code) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("code", code);

        return queryForObject("system/common", "getTenantByCode", paramMap,
                (resultSet, rowIndex) -> createTenant(resultSet));
    }

    @Override
    public User getUser(final Tenant tenant, final String code) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("tenant_Id", tenant.getId());
        paramMap.put("code", code);

        List<User> list = new NamedParameterJdbcTemplate(builder.getDataSource(tenant.getReadOnlyDbServerName()))
                .query(SqlHelper.getSql("system/common", "getUserByCode", builder.getDbmsName(tenant.getReadOnlyDbServerName()), paramMap),
                        paramMap,
                        (resultSet, rowIndex) -> {
                            User user = new User();
                            user.setId(resultSet.getLong("Operator_Id"));
                            user.setCode(resultSet.getString("Operator_Code"));
                            user.setName(resultSet.getString("Operator_Name"));
                            user.setUserPassword(resultSet.getString("User_Password"));
                            user.setTenant(tenant);
                            return user;
                        });

        return list.size() > 0 ? list.get(0) : null;
    }
}
