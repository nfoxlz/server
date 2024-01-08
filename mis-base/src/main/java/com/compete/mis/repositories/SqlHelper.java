package com.compete.mis.repositories;

import com.compete.mis.util.Global;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public final class SqlHelper {

    private static final String sqlPath = Global.getLocalPath("plugins/%s/%s/%s.sql");

    private static final String paramPath = Global.getLocalPath("plugins/%s/%s/%s.param");

    public static final String sysParamPath = Global.getLocalPath("plugins/%s/%s/%s.sp");//classpath:

    public static final String relatedParamPath = Global.getLocalPath("plugins/%s/%s/%s.rp");//classpath:

    /**
     * 数据源构建器。
     */
    @Autowired
    private DataSourceBuilder builder;

    private static final Cache<String, String> sqlCache = Caffeine
            .newBuilder()
            .maximumSize(1024)
            .expireAfterWrite(2, TimeUnit.HOURS)
//            .build(key -> Files.readString(Paths.get(key)));
//            .build(key -> Files.readString(ResourceUtils.getFile(key).toPath()));
            .build();


    private static final Cache<String, Map<String, String>> paramCache = Caffeine
            .newBuilder()
            .maximumSize(1024)
            .expireAfterWrite(2, TimeUnit.HOURS)
            .build();

    private static final Cache<String, Map<String, String>> sysParamCache = Caffeine
            .newBuilder()
            .maximumSize(1024)
            .expireAfterWrite(2, TimeUnit.HOURS)
            .build();

    private static final Cache<String, Map<String, String>> relatedParamCache = Caffeine
            .newBuilder()
            .maximumSize(1024)
            .expireAfterWrite(2, TimeUnit.HOURS)
            .build();

    public static void clearCache() {
        sqlCache.invalidateAll();
        paramCache.invalidateAll();
        sysParamCache.invalidateAll();
        relatedParamCache.invalidateAll();
//        sqlCache.cleanUp();
//        paramCache.cleanUp();
//        sysParamCache.cleanUp();
    }

    private static Map<String, String> readParameterFile(final String name) throws IOException {
        try {
            List<String> lines = Files.readAllLines(ResourceUtils.getFile(name).toPath());

            Map<String, String> result = new HashMap<>();
            int index;
            for (String line : lines) {

                if ("".equals(line.trim()))
                    continue;

                index = line.indexOf("=");
                if (index > 0)
                    result.put(line.substring(0, index).trim(), line.substring(index + 1));
                else
                    result.put(line.trim(), null);
            }
            return result;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    private static Map<String, String> getParameters(final String path) {
        return paramCache.get(path, key -> {
            try {
                return readParameterFile(key);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static Map<String, String> getSystemParameters(final String path) {
        return sysParamCache.get(path, key -> {
            try {
                return readParameterFile(key);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static Map<String, String> getRelatedParameters(final String path) {
        return relatedParamCache.get(path, key -> {
            try {
                return readParameterFile(key);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static String getSql(final String path) {
        return sqlCache.get(path, key -> {
            try {
                return Files.readString(ResourceUtils.getFile(key).toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static String getBaseName(final String name) {
        int index = name.indexOf('.');
        return index < 1 ? null : name.substring(0, index);
    }

    public static String getSql(final String path, final String name, final String dbmsName, final Map<String, ?> paramMap) {

        String sql = getSql(sqlPath.formatted(path, dbmsName, name));
        if (paramMap != null)
        {
            String paramName = null;
            if (existsParam(path, name, dbmsName))
                paramName = name;
            else {
                String baseName = getBaseName(name);
                if (baseName != null && existsParam(path, baseName, dbmsName))
                    paramName = baseName;
            }

            if (paramName != null)
                for (Map.Entry<String, String> entry : getParameters(String.format(paramPath, path, dbmsName, paramName)).entrySet())
                    sql = sql.replace("{" + entry.getKey() + "}", paramMap.containsKey(entry.getKey()) ? entry.getValue() : "");
        }

        return sql;
    }

    public String getSql(final String path, final String name, final Map<String, ?> paramMap) throws IOException {
        return getSql(path, name, builder.getDbmsName(), paramMap);
    }

    public String getReadOnlySql(final String path, final String name, final Map<String, ?> paramMap) throws IOException {
        return getSql(path, name, builder.getReadOnlyDbmsName(), paramMap);
    }

    public static boolean exists(final String path) {
        try {
            return sqlCache.getIfPresent(path) != null || ResourceUtils.getFile(path).exists();
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public static boolean exists(final String path, final String name, final String dbmsName) {
        return exists(String.format(sqlPath, path, dbmsName, name));
    }

    public boolean exists(final String path, final String name) throws IOException {
        return exists(path, name, builder.getDbmsName());
    }

    public boolean existsReadOnly(final String path, final String name) throws IOException {
        return exists(path, name, builder.getReadOnlyDbmsName());
    }

    public static boolean existsParam(final String path) {
        try {
            return paramCache.getIfPresent(path) != null || ResourceUtils.getFile(path).exists();//new File(path).exists();//Files.exists(Paths.get(path));
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public static boolean existsParam(final String path, final String name, final String dbmsName) {
        return existsParam(String.format(paramPath, path, dbmsName, name));
    }

    public boolean existsParam(final String path, final String name) throws IOException {
        return existsParam(path, name, builder.getDbmsName());
    }

    public boolean existsParamReadOnly(final String path, final String name) throws IOException {
        return existsParam(path, name, builder.getReadOnlyDbmsName());
    }

    public static boolean existsSysParam(final String path) {
        try {
            return sysParamCache.getIfPresent(path) != null || ResourceUtils.getFile(path).exists();//new File(path).exists();//Files.exists(Paths.get(path));
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public static boolean existsSysParam(final String path, final String name, final String dbmsName) {
        return existsSysParam(String.format(sysParamPath, path, dbmsName, name));
    }

    public boolean existsSysParam(final String path, final String name) throws IOException {
        return existsSysParam(path, name, builder.getDbmsName());
    }

    public boolean existsSysParamReadOnly(final String path, final String name) throws IOException {
        return existsSysParam(path, name, builder.getReadOnlyDbmsName());
    }

    public static boolean existsRelatedParam(final String path) {
        try {
            return relatedParamCache.getIfPresent(path) != null || ResourceUtils.getFile(path).exists();//new File(path).exists();//Files.exists(Paths.get(path));
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public static boolean existsRelatedParam(final String path, final String name, final String dbmsName) {
        return existsRelatedParam(relatedParamPath.formatted(path, dbmsName, name));
    }

    public boolean existsRelatedParam(final String path, final String name) throws IOException {
        return existsRelatedParam(path, name, builder.getDbmsName());
    }

    public boolean existsRelatedParamReadOnly(final String path, final String name) throws IOException {
        return existsRelatedParam(path, name, builder.getReadOnlyDbmsName());
    }

    public static Map<String, String> getRelatedParam(final String path, final String name, final String dbmsName) {
        return getRelatedParameters(relatedParamPath.formatted(path, dbmsName, name));
    }

    public Map<String, String> getRelatedParam(final String path, final String name) throws IOException {
        return getRelatedParam(path, name, builder.getDbmsName());
    }

    public Map<String, String> getRelatedParamReadOnly(final String path, final String name) throws IOException {
        return getRelatedParam(path, name, builder.getReadOnlyDbmsName());
    }

    public static String getEntitySql(final String entityName, final String path, final String name, final String dbmsName) {
        return String.format(getSql(path, name, dbmsName, null), entityName);
    }

    public String getEntitySql(final String entityName, final String path, final String name) throws IOException {
        return getEntitySql(entityName, path, name, builder.getDbmsName());
    }

    public String getEntitySqlReadOnly(final String entityName, final String path, final String name) throws IOException {
        return getEntitySql(entityName, path, name, builder.getReadOnlyDbmsName());
    }
}
