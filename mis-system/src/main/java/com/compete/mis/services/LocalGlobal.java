package com.compete.mis.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

public final class LocalGlobal {

    private static ObjectMapper objectMapper = new ObjectMapper();

    private static final Cache<String, SaveConfig> saveConfigCache = Caffeine
            .newBuilder()
            .maximumSize(1024)
            .expireAfterWrite(2, TimeUnit.HOURS)
            .build();

    public static void clearCache() {
        saveConfigCache.invalidateAll();
    }

    public static SaveConfig getSqlConfig(final String path) {
        return saveConfigCache.get(path, key -> {
            try {
                return ResourceUtils.getFile(key).exists()
                        ? objectMapper.readValue(Files.readString(ResourceUtils.getFile(key).toPath()), SaveConfig.class)
                        : new SaveConfig();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
