package com.weiquding.safeKeyboard.common.util;

import org.springframework.util.Assert;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

/**
 * 安全传输工具类
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/12
 */
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


        return null;
    }

    private static void checkPreConditions(PrivateKey privateKey, PublicKey publicKey, Map<String, Object> message) {
        Assert.isInstanceOf(RSAPrivateKey.class, privateKey, "privateKey must be an instance of RSAPrivateKey");
        Assert.isInstanceOf(RSAPublicKey.class, publicKey, "publicKey must be an instance of RSAPublicKey");
        Assert.notEmpty(message, "message must not be empty");
    }

}
