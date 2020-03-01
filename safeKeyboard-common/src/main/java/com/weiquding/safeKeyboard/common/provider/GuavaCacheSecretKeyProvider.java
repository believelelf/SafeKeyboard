package com.weiquding.safeKeyboard.common.provider;

import com.weiquding.safeKeyboard.common.cache.GuavaCache;
import com.weiquding.safeKeyboard.common.exception.BaseBPError;
import com.weiquding.safeKeyboard.common.util.MyBase64;
import com.weiquding.safeKeyboard.common.util.RandomUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * description
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/3/1
 */
public class GuavaCacheSecretKeyProvider implements SecretKeyProvider {

    @SuppressWarnings("unchecked")
    @Override
    public byte[] getSecretKey(String key, boolean isNewCreate) {
        Map<String, String> cipherMap = (Map<String, String>) GuavaCache.CLIENT_CACHE.getIfPresent(key);
        byte[] cipherBytes = null;
        if (cipherMap != null) {
            cipherBytes = MyBase64.getDecoder().decode(cipherMap.get(key));
        } else if (isNewCreate) {
            cipherBytes = RandomUtil.generateRandomBytes(16);
            cipherMap = new HashMap<>();
            cipherMap.put(key, MyBase64.getEncoder().encodeToString(cipherBytes));
            GuavaCache.CLIENT_CACHE.put(key, cipherMap);
        } else {
            throw BaseBPError.ENCRYPTION_KEY.getInfo().initialize();
        }
        return cipherBytes;
    }
}
