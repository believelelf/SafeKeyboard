package com.weiquding.safeKeyboard.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.weiquding.safeKeyboard.common.dto.EncryptAndSignatureDto;
import com.weiquding.safeKeyboard.common.exception.BaseBPError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;

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
     * @param appId      应用ID
     * @param message    数据
     * @return 加密及签名结果
     */
    public static EncryptAndSignatureDto encryptAndSignature(PrivateKey privateKey, PublicKey publicKey, String appId, Object message) {
        checkPreConditions(privateKey, publicKey, message);

        Assert.notNull(appId, "appId must not be empty");

        // 产生AES对称密钥
        byte[] secretKey = AESUtil.AES_256_CBC_PKCS5Padding.initAESKey();
        log.info("产生AES对称密钥[{}]", Arrays.toString(secretKey));
        // AES对称加密数据
        byte[] data = null;
        try {
            data = JsonUtil.getObjectMapper().writeValueAsBytes(message);
        } catch (JsonProcessingException e) {
            throw BaseBPError.PROCESSING_JSON_DATA.getInfo().initialize(e);
        }
        byte[] encryptedData = AESUtil.AES_256_CBC_PKCS5Padding.encryptByAESKey(secretKey, data);

        // 对方RSA公钥对AES密钥进行非对称加密
        byte[] encryptedKey = RSAUtil.encryptByRSAPublicKey((RSAPublicKey) publicKey, secretKey);

        // 本方RSA私钥对数据进行签名
        byte[] signature = RSAUtil.signByRSAPrivateKey((RSAPrivateKey) privateKey, data);

        return new EncryptAndSignatureDto(
                appId,
                MyBase64.getUrlEncoder().encodeToString(signature),
                MyBase64.getUrlEncoder().encodeToString(encryptedKey),
                MyBase64.getUrlEncoder().encodeToString(encryptedData)
        );
    }

    /**
     * AES对称解密数据，RSA非对称解密AES密钥，SHA256withRSA验签数据
     *
     * @param privateKey 本方RSA私钥
     * @param publicKey  对方RSA公钥
     * @param message    加密数据
     * @param valueType  数据类型
     * @return 解密数据
     */
    @SuppressWarnings("unchecked")
    public static <T> T decryptAndVerifySign(PrivateKey privateKey, PublicKey publicKey, EncryptAndSignatureDto message, Class<T> valueType) {
        checkPreConditions(privateKey, publicKey, message);

        String encryptedKey = message.getEncryptedKey();
        String encryptedData = message.getEncryptedData();
        String signature = message.getSignature();

        if (encryptedKey == null || encryptedData == null || signature == null) {
            throw new IllegalArgumentException("Invalid input parameter");
        }

        // 本方RSA私钥解密AES密钥
        byte[] secretKey = RSAUtil.decryptByRSAPrivateKey((RSAPrivateKey) privateKey, MyBase64.getUrlDecoder().decode(encryptedKey));
        log.info("本方RSA私钥解密AES密钥[{}]", Arrays.toString(secretKey));

        // AES对称解密数据
        byte[] data = AESUtil.AES_256_CBC_PKCS5Padding.decryptByAESKey(secretKey, MyBase64.getUrlDecoder().decode(encryptedData));

        // 对方RSA公钥验签数据及签名
        boolean isVerifyed = RSAUtil.verifySignByRSAPublicKey((RSAPublicKey) publicKey, data, MyBase64.getUrlDecoder().decode(signature));
        if (!isVerifyed) {
            throw new IllegalStateException("Invalid data");
        }
        try {
            return JsonUtil.getObjectMapper().readValue(data, valueType);
        } catch (IOException e) {
            throw BaseBPError.PROCESSING_JSON_DATA.getInfo().initialize(e);
        }
    }


    private static void checkPreConditions(PrivateKey privateKey, PublicKey publicKey, Object message) {
        Assert.isInstanceOf(RSAPrivateKey.class, privateKey, "privateKey must be an instance of RSAPrivateKey");
        Assert.isInstanceOf(RSAPublicKey.class, publicKey, "publicKey must be an instance of RSAPublicKey");
        Assert.notNull(message, "message must not be null");
    }


}
