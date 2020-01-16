package com.weiquding.safeKeyboard.common.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiquding.safeKeyboard.common.annotation.DecryptSafeFields;
import com.weiquding.safeKeyboard.common.annotation.EncryptSafeFields;
import com.weiquding.safeKeyboard.common.cache.GuavaCache;
import com.weiquding.safeKeyboard.common.exception.SafeBPError;
import com.weiquding.safeKeyboard.common.util.AESUtil;
import com.weiquding.safeKeyboard.common.util.Base64;
import com.weiquding.safeKeyboard.common.util.RandomUtil;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于GuavaCache及AES算法的安全提供者
 *
 * @author wuby
 * @version V1.0
 * @date 2020/1/3
 */
@Component("safeProvider")
public class GuavaCacheAESSafeProvider implements SafeProvider {

    private static String CIPHER_FIELD = "cipher";

    private static String ALLOW_URI = "allow_uri";

    @Override
    public String encryptSafeFields(Map<String, Object> model, EncryptSafeFields metadata, String requestUri, String sessionId) {
        String[] fields = metadata.fields();
        String name = metadata.name();
        model.put(ALLOW_URI, requestUri);
        try {
            byte[] bytes = new ObjectMapper().writeValueAsBytes(model);
            byte[] cipherBytes = null;
            String cipherKey = sessionId + ":" + CIPHER_FIELD;
            Map<String, String> cipherMap = GuavaCache.CLIENT_CACHE.getIfPresent(cipherKey);
            if (cipherMap != null) {
                cipherBytes = Base64.getDecoder().decode(cipherMap.get(CIPHER_FIELD));
            } else {
                cipherBytes = RandomUtil.generateRandomBytes(16);
                cipherMap = new HashMap<>();
                cipherMap.put(CIPHER_FIELD, Base64.getEncoder().encodeToString(cipherBytes));
                GuavaCache.CLIENT_CACHE.put(cipherKey, cipherMap);
            }

            byte[] iv = AESUtil.AES_256_GCM_NoPadding.ivParameter();
            byte[] encryptedMsg = AESUtil.AES_256_GCM_NoPadding.encryptByAESKey(cipherBytes, iv, bytes);
            byte[] newEncryptedMsg = new byte[iv.length + encryptedMsg.length];
            System.arraycopy(iv, 0, newEncryptedMsg, 0, iv.length);
            System.arraycopy(encryptedMsg, 0, newEncryptedMsg, iv.length, encryptedMsg.length);
            return Base64.getUrlEncoder().encodeToString(newEncryptedMsg);
        } catch (JsonProcessingException e) {
            throw SafeBPError.PROCESSING_JSON_DATA.getInfo().initialize(e);
        }
    }

    @Override
    public Map<String, Object> decryptSafeFields(Map<String, Object> model, DecryptSafeFields metadata, String sessionId) {
        String newEncryptedMsg = (String) model.get(metadata.name());
        if (newEncryptedMsg == null) {
            throw new IllegalArgumentException("The encryption field is null");
        }
        byte[] bytes = Base64.getUrlDecoder().decode(newEncryptedMsg);
        byte[] iv = Arrays.copyOf(bytes, 12);
        byte[] encryptedMsg = Arrays.copyOfRange(bytes, 12, bytes.length);
        String cipherKey = sessionId + ":" + CIPHER_FIELD;
        Map<String, String> cipherMap = GuavaCache.CLIENT_CACHE.getIfPresent(cipherKey);
        if (cipherMap == null) {
            throw SafeBPError.ENCRYPTION_KEY.getInfo().initialize();
        }
        byte[] cipher = Base64.getDecoder().decode(cipherMap.get(CIPHER_FIELD));
        byte[] decryptedMsg = AESUtil.AES_256_GCM_NoPadding.decryptByAESKey(cipher, iv, encryptedMsg);
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> params = new ObjectMapper().readValue(decryptedMsg, Map.class);
            boolean isAllowUri = checkAllowUri(params.get(ALLOW_URI), metadata.allowUris());
            if (!isAllowUri) {
                throw SafeBPError.ALLOW_URI.getInfo().initialize();
            }
            return params;
        } catch (IOException e) {
            throw SafeBPError.PROCESSING_JSON_DATA.getInfo().initialize(e);
        }

    }

    private boolean checkAllowUri(Object uri, String[] allowUris) {
        if (uri == null) {
            return true;
        }
        for (String allowUri : allowUris) {
            if (allowUri.equals(uri)) {
                return true;
            }
        }
        return false;
    }
}
