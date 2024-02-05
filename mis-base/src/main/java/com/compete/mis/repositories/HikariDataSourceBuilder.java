package com.compete.mis.repositories;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * 使用Hikari连接池的数据源构建器类。
 * @Author: Lee Zheng.
 */
@Component
public final class HikariDataSourceBuilder implements DataSourceBuilder {

//    private static final Logger LOGGER = LoggerFactory.getLogger(HikariDataSourceBuilder.class);

    private static Cache<String, DataSource> dataSourceCache = Caffeine
            .newBuilder()
            .maximumSize(128)
            .expireAfterWrite(24, TimeUnit.HOURS)
            .build(key -> {
                Properties properties = new Properties();
                // 读取配置文件。
                try (InputStream stream = HikariDataSourceBuilder.class.getResourceAsStream(String.format("/database.%s.properties", key))) {
                    properties.load(stream);
                }

                HikariConfig config = new HikariConfig();
                config.setDriverClassName(properties.getProperty("jdbc.drivers", "org.postgresql.Driver"));
                config.setJdbcUrl(properties.getProperty("jdbc.url", "jdbc:postgresql://localhost:5432/business"));
                config.setUsername(properties.getProperty("jdbc.username", "superman"));
                config.setPassword(properties.getProperty("jdbc.password", "1qaz@WSX123"));

                return new HikariDataSource(config);
            });

    private static Cache<String, String> dbmsNameCache = Caffeine
            .newBuilder()
            .maximumSize(128)
            .expireAfterWrite(24, TimeUnit.HOURS)
            .build(key -> {
                Properties properties = new Properties();
                // 读取配置文件。
                try (InputStream stream = HikariDataSourceBuilder.class.getResourceAsStream(String.format("/database.%s.properties", key))) {
                    properties.load(stream);
                }

                return properties.getProperty("dbmsName", "postgres");
            });

    public static void clearCache() {
        dataSourceCache.invalidateAll();
        dbmsNameCache.invalidateAll();
//        dataSourceCache.cleanUp();
//        dbmsNameCache.cleanUp();
    }

    /**
     * 取得指定名称的数据源。
     *
     * @param name 数据源名称。即“src/main/resources/database.{name}.properties”中的{name}。
     * @return 数据源。
     */
    @Override
    public DataSource getDataSource(String name) {
        return dataSourceCache.get(name, key -> {
            Properties properties = new Properties();
            // 读取配置文件。
            try (InputStream stream = HikariDataSourceBuilder.class.getResourceAsStream(String.format("/database.%s.properties", key))) {
                properties.load(stream);
            } catch (IOException e) {
//                LOGGER.error(String.format("【/database.%s.properties】文件打开失败。", key));
//                LOGGER.error(e.getMessage());
                throw new RuntimeException(e);
            }

            HikariConfig config = new HikariConfig();
            config.setDriverClassName(properties.getProperty("jdbc.drivers", "org.postgresql.Driver"));
            config.setJdbcUrl(properties.getProperty("jdbc.url", String.format("jdbc:postgresql://localhost:5432/%s", name)));
            config.setUsername(properties.getProperty("jdbc.username", "superman"));
            config.setPassword(properties.getProperty("jdbc.password", "1qaz@WSX123"));

            return new HikariDataSource(config);
        });
    }

    @Override
    public String getDbmsName(String name) {
        return dbmsNameCache.get(name, key -> {
            Properties properties = new Properties();
            // 读取配置文件。
            try (InputStream stream = HikariDataSourceBuilder.class.getResourceAsStream(String.format("/database.%s.properties", key))) {
                properties.load(stream);
            } catch (IOException e) {
//                LOGGER.error(String.format("【/database.%s.properties】文件打开失败。", key));
//                LOGGER.error(e.getMessage());
//                e.printStackTrace();
                throw new RuntimeException(e);
            }

            return properties.getProperty("dbmsName", "postgres");
        });
    }
}
