package com.weiquding.safeKeyboard.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.weiquding.safeKeyboard.common.dto.EncryptAndSignatureDto;
import com.weiquding.safeKeyboard.common.exception.BaseBPError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.List;
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

    /**
     * 应用ID
     */
    public static final String APP_ID = "appId";
    /**
     * 数据签名
     */
    private static final String SIGNATURE = "signature";
    /**
     * 对称密钥密文
     */
    private static final String ENCRYPTED_KEY = "encryptedKey";
    /**
     * 数据密文
     */
    private static final String ENCRYPTED_DATA = "encryptedData";


    /**
     * AES对称加密数据，RSA非对称加密AES密钥，SHA256withRSA签名数据
     *
     * @param privateKey 本方RSA私钥
     * @param publicKey  对方RSA公钥
     * @param appId      应用ID
     * @param message    数据
     * @return 加密及签名结果
     */
    public static <P> EncryptAndSignatureDto<String, P> encryptAndSignature(PrivateKey privateKey, PublicKey publicKey, String appId, Object message) {
        checkPreConditions(privateKey, publicKey, message);

        Assert.notNull(appId, "appId must not be empty");

        // 产生AES对称密钥
        byte[] secretKey = AESUtil.AES_256_CBC_PKCS5Padding.initAESKey();
        log.debug("产生AES对称密钥[{}]", Arrays.toString(secretKey));
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

        return new EncryptAndSignatureDto<>(
                appId,
                MyBase64.getUrlEncoder().encodeToString(signature),
                MyBase64.getUrlEncoder().encodeToString(encryptedKey),
                MyBase64.getUrlEncoder().encodeToString(encryptedData)
        );
    }

    /**
     * AES对称加密数据，RSA非对称加密AES密钥，SHA256withRSA签名数据
     * 针对文件请求
     *
     * @param privateKey 本方RSA私钥
     * @param publicKey  对方RSA公钥
     * @param appId      应用ID
     * @param originFile 源文件
     * @param fileName   加密后文件名
     * @return 加密及签名结果
     */
    public static MultiValueMap<String, Object> encryptAndSignature4File(PrivateKey privateKey, PublicKey publicKey, String appId, File originFile, String fileName) {
        checkPreConditions(privateKey, publicKey, originFile);

        Assert.notNull(appId, "appId must not be empty");
        Assert.notNull(fileName, "fileName must not be empty");

        // 产生AES对称密钥
        byte[] secretKey = AESUtil.AES_256_CBC_PKCS5Padding.initAESKey();
        log.debug("产生AES对称密钥[{}]", Arrays.toString(secretKey));
        // AES对称加密数据
        byte[] data = ZipUtil.zipping(originFile);
        byte[] encryptedData = AESUtil.AES_256_CBC_PKCS5Padding.encryptByAESKey(secretKey, data);

        // 对方RSA公钥对AES密钥进行非对称加密
        byte[] encryptedKey = RSAUtil.encryptByRSAPublicKey((RSAPublicKey) publicKey, secretKey);

        // 本方RSA私钥对数据进行签名
        byte[] signature = RSAUtil.signByRSAPrivateKey((RSAPrivateKey) privateKey, data);

        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add(APP_ID, appId);
        multiValueMap.add(SIGNATURE, MyBase64.getUrlEncoder().encodeToString(signature));
        multiValueMap.add(ENCRYPTED_KEY, MyBase64.getUrlEncoder().encodeToString(encryptedKey));
        multiValueMap.add(ENCRYPTED_DATA, new ByteArrayResource(encryptedData) {
            @Override
            public String getFilename() {
                return fileName;
            }
        });

        return multiValueMap;
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
    public static <T, P> T decryptAndVerifySign(PrivateKey privateKey, PublicKey publicKey, EncryptAndSignatureDto<String, P> message, Class<T> valueType) {
        checkPreConditions(privateKey, publicKey, message);

        String encryptedKey = message.getEncryptedKey();
        String encryptedData = message.getEncryptedData();
        String signature = message.getSignature();

        if (encryptedKey == null || encryptedData == null || signature == null) {
            throw new IllegalArgumentException("Invalid input parameter");
        }

        // 本方RSA私钥解密AES密钥
        byte[] secretKey = RSAUtil.decryptByRSAPrivateKey((RSAPrivateKey) privateKey, MyBase64.getUrlDecoder().decode(encryptedKey));
        log.debug("本方RSA私钥解密AES密钥[{}]", Arrays.toString(secretKey));

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

    /**
     * AES对称解密数据，RSA非对称解密AES密钥，SHA256withRSA验签数据
     * 针对文件请求
     *
     * @param privateKey    本方RSA私钥
     * @param publicKey     对方RSA公钥
     * @param encryptedData 加密文件
     * @param message       加密数据
     * @return 解密数据
     */
    @SuppressWarnings("unchecked")
    public static List<ZipUtil.FileData> decryptAndVerifySign4File(PrivateKey privateKey, PublicKey publicKey, MultipartFile encryptedData, Map<String, Object> message) {
        checkPreConditions(privateKey, publicKey, message);

        String encryptedKey = (String) message.get(ENCRYPTED_KEY);
        String signature = (String) message.get(SIGNATURE);

        if (encryptedKey == null || encryptedData == null || signature == null) {
            throw new IllegalArgumentException("Invalid input parameter");
        }

        // 本方RSA私钥解密AES密钥
        byte[] secretKey = RSAUtil.decryptByRSAPrivateKey((RSAPrivateKey) privateKey, MyBase64.getUrlDecoder().decode(encryptedKey));
        log.debug("本方RSA私钥解密AES密钥[{}]", Arrays.toString(secretKey));
        try {
            // AES对称解密数据
            byte[] data = AESUtil.AES_256_CBC_PKCS5Padding.decryptByAESKey(secretKey, encryptedData.getBytes());

            // 对方RSA公钥验签数据及签名
            boolean isVerifyed = RSAUtil.verifySignByRSAPublicKey((RSAPublicKey) publicKey, data, MyBase64.getUrlDecoder().decode(signature));
            if (!isVerifyed) {
                throw new IllegalStateException("Invalid data");
            }
            // 解压缩文件
            return ZipUtil.unzip(data);
        } catch (IOException e) {
            throw BaseBPError.ACCESS_ERRORS.getInfo().initialize(e);
        }
    }


    private static void checkPreConditions(PrivateKey privateKey, PublicKey publicKey, Object message) {
        Assert.isInstanceOf(RSAPrivateKey.class, privateKey, "privateKey must be an instance of RSAPrivateKey");
        Assert.isInstanceOf(RSAPublicKey.class, publicKey, "publicKey must be an instance of RSAPublicKey");
        Assert.notNull(message, "message must not be null");
    }


}
