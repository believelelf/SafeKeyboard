package com.weiquding.safeKeyboard.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.weiquding.safeKeyboard.common.exception.CipherRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 安全传输工具类
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/12
 */
@Slf4j
public class SecureUtil {

    public static final String APPID_KEY = "appId";

    public static final String SIGNATURE = "signature";

    public static final String ENCRYPTED_KEY = "encryptedKey";

    public static final String ENCRYPTED_DATA = "encryptedData";


    /**
     * AES对称加密数据，RSA非对称加密AES密钥，SHA256withRSA签名数据
     *
     * @param privateKey 本方RSA私钥
     * @param publicKey  对方RSA公钥
     * @param message    数据
     * @return 加密及签名结果
     */
    public static Map<String, Object> encryptAndSignature(PrivateKey privateKey, PublicKey publicKey, Map<String, Object> message) {
        checkPreConditions(privateKey, publicKey, message);

        String appId = (String) message.get(APPID_KEY);
        Assert.notNull(appId, "appId must not be empty");

        // 产生AES对称密钥
        byte[] secretKey = AESUtil.AES_256_CBC_PKCS5Padding.initAESKey();
        log.info("产生AES对称密钥[{}]", Arrays.toString(secretKey));
        // AES对称加密数据
        byte[] data = null;
        try {
            data = JsonUtil.getObjectMapper().writeValueAsBytes(message);
        } catch (JsonProcessingException e) {
            throw new CipherRuntimeException("An error occurred while processing json data", e);
        }
        byte[] encryptedData = AESUtil.AES_256_CBC_PKCS5Padding.encryptByAESKey(secretKey, data);

        // 对方RSA公钥对AES密钥进行非对称加密
        byte[] encryptedKey = RSAUtil.encryptByRSAPublicKey((RSAPublicKey) publicKey, secretKey);

        // 本方RSA私钥对数据进行签名
        byte[] signature = RSAUtil.signByRSAPrivateKey((RSAPrivateKey) privateKey, data);

        Map<String, Object> result = new HashMap<>();
        result.put(APPID_KEY, appId);
        result.put(ENCRYPTED_DATA, Base64.getUrlEncoder().encodeToString(encryptedData));
        result.put(ENCRYPTED_KEY, Base64.getUrlEncoder().encodeToString(encryptedKey));
        result.put(SIGNATURE, Base64.getUrlEncoder().encodeToString(signature));
        return result;
    }

    /**
     * AES对称解密数据，RSA非对称解密AES密钥，SHA256withRSA验签数据
     *
     * @param privateKey 本方RSA私钥
     * @param publicKey  对方RSA公钥
     * @param message    数据
     * @return 解密数据
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> decryptAndVerifySign(PrivateKey privateKey, PublicKey publicKey, Map<String, Object> message) {
        checkPreConditions(privateKey, publicKey, message);

        String encryptedKey = (String) message.get(ENCRYPTED_KEY);
        String encryptedData = (String) message.get(ENCRYPTED_DATA);
        String signature = (String) message.get(SIGNATURE);

        if (encryptedKey == null || encryptedData == null || signature == null) {
            throw new IllegalArgumentException("Invalid input parameter");
        }

        // 本方RSA私钥解密AES密钥
        byte[] secretKey = RSAUtil.decryptByRSAPrivateKey((RSAPrivateKey) privateKey, Base64.getUrlDecoder().decode(encryptedKey));
        log.info("本方RSA私钥解密AES密钥[{}]", Arrays.toString(secretKey));

        // AES对称解密数据
        byte[] data = AESUtil.AES_256_CBC_PKCS5Padding.decryptByAESKey(secretKey, Base64.getUrlDecoder().decode(encryptedData));

        // 对方RSA公钥验签数据及签名
        boolean isVerifyed = RSAUtil.verifySignByRSAPublicKey((RSAPublicKey) publicKey, data, Base64.getUrlDecoder().decode(signature));
        if (!isVerifyed) {
            throw new IllegalStateException("Invalid data");
        }
        try {
            return JsonUtil.getObjectMapper().readValue(data, Map.class);
        } catch (IOException e) {
            throw new CipherRuntimeException("An error occurred while processing json data", e);
        }
    }


    private static void checkPreConditions(PrivateKey privateKey, PublicKey publicKey, Map<String, Object> message) {
        Assert.isInstanceOf(RSAPrivateKey.class, privateKey, "privateKey must be an instance of RSAPrivateKey");
        Assert.isInstanceOf(RSAPublicKey.class, publicKey, "publicKey must be an instance of RSAPublicKey");
        Assert.notEmpty(message, "message must not be empty");
    }

}
