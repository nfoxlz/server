package com.compete.mis.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class ErrorManager {

    private static final Map<Integer, String[]> messageMap;

    private static final Cache<String, Map<Integer, String[]>> messageCache = Caffeine
            .newBuilder()
            .maximumSize(1024)
            .expireAfterWrite(2, TimeUnit.HOURS)
            .build();

    static {
        try {
            messageMap = getMessageMap("errors");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void clearCache() {
        messageCache.cleanUp();
    }

    public static Map<Integer, String[]> getMessageMap(final String file) throws IOException {
        String[] errorList;
        String[] messages;

        Map<Integer, String[]> result = new HashMap<>();
        List<String> lines = Files.readAllLines(ResourceUtils.getFile(Global.getLocalPath(file)).toPath());

        for (String line : lines) {
            errorList = line.split(":");
            if (2 > errorList.length)
                continue;

            messages = errorList[1].split(",");

            if (0 < messages.length)
                result.put(Integer.parseInt(errorList[0]), messages);
        }

        return result;
    }

    public static String getMessage(final String path, final int no, final Map<String, Object> param) {

        Map<Integer, String[]> pathMessageMap = messageCache.get(path, key -> {
            try {
                return getMessageMap("%s/errors".formatted(path));
            } catch (IOException e) {
                return null;
            }
        });

        String[] messages = null == pathMessageMap ? messageMap.get(no) : pathMessageMap.get(no);

        if (null == messages || 0 == messages.length)
            return null;

        List<Object> messageParam = new ArrayList<>();
        int length = messages.length;
        for (int i = 1; i < length; i++)
            messageParam.add(param.get(messages[i]));
        return messages[0].formatted(messageParam.toArray());
    }
}
