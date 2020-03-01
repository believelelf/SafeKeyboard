package com.weiquding.safeKeyboard.common.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiquding.safeKeyboard.common.annotation.DecryptSafeFields;
import com.weiquding.safeKeyboard.common.annotation.EncryptSafeFields;
import com.weiquding.safeKeyboard.common.exception.BaseBPError;
import com.weiquding.safeKeyboard.common.util.AESUtil;
import com.weiquding.safeKeyboard.common.util.MyBase64;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
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

    private static final String CIPHER_FIELD = "cipher";

    private static final String ALLOW_URI = "allow_uri";

    private SecretKeyProvider secretKeyProvider;

    public GuavaCacheAESSafeProvider(SecretKeyProvider secretKeyProvider) {
        this.secretKeyProvider = secretKeyProvider;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String encryptSafeFields(Map<String, Object> model, EncryptSafeFields metadata, String requestUri, String sessionId) {
        model.put(ALLOW_URI, requestUri);
        try {
            byte[] bytes = new ObjectMapper().writeValueAsBytes(model);
            byte[] cipherBytes = secretKeyProvider.getSecretKey(sessionId + ":" + CIPHER_FIELD, true);
            byte[] iv = AESUtil.AES_256_GCM_NoPadding.ivParameter();
            byte[] encryptedMsg = AESUtil.AES_256_GCM_NoPadding.encryptByAESKey(cipherBytes, iv, bytes);
            byte[] newEncryptedMsg = new byte[iv.length + encryptedMsg.length];
            System.arraycopy(iv, 0, newEncryptedMsg, 0, iv.length);
            System.arraycopy(encryptedMsg, 0, newEncryptedMsg, iv.length, encryptedMsg.length);
            return MyBase64.getUrlEncoder().encodeToString(newEncryptedMsg);
        } catch (JsonProcessingException e) {
            throw BaseBPError.PROCESSING_JSON_DATA.getInfo().initialize(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> decryptSafeFields(String safeFieldValue, DecryptSafeFields metadata, String sessionId) {
        if (safeFieldValue == null) {
            throw new IllegalArgumentException("The encryption field is null");
        }
        byte[] bytes = MyBase64.getUrlDecoder().decode(safeFieldValue);
        byte[] iv = Arrays.copyOf(bytes, 12);
        byte[] encryptedMsg = Arrays.copyOfRange(bytes, 12, bytes.length);
        byte[] cipherBytes = secretKeyProvider.getSecretKey(sessionId + ":" + CIPHER_FIELD, false);
        byte[] decryptedMsg = AESUtil.AES_256_GCM_NoPadding.decryptByAESKey(cipherBytes, iv, encryptedMsg);
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> params = new ObjectMapper().readValue(decryptedMsg, Map.class);
            boolean isAllowUri = checkAllowUri(params.get(ALLOW_URI), metadata.allowUris());
            if (!isAllowUri) {
                throw BaseBPError.ALLOW_URI.getInfo().initialize();
            }
            return params;
        } catch (IOException e) {
            throw BaseBPError.PROCESSING_JSON_DATA.getInfo().initialize(e);
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
