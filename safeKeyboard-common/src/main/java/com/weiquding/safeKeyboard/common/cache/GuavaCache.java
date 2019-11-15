package com.weiquding.safeKeyboard.common.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 缓存
 * @author beliveyourself
 */
public class GuavaCache {

    /**
     * CLIENT CACHE
     */
    public static final Cache<String, Map<String, String>> CLIENT_CACHE = CacheBuilder.newBuilder()
                  .maximumSize(1000)
                  .expireAfterAccess(300,TimeUnit.SECONDS)
                  .build();
    /**
     * SERVER CACHE
     */
    public static final Cache<String, Map<String, String>> SERVER_CACHE = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(300,TimeUnit.SECONDS)
            .build();
}
