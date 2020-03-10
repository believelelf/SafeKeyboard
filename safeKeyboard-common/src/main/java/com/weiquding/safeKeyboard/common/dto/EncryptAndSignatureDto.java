package com.weiquding.safeKeyboard.common.dto;

import lombok.Data;

/**
 * 密文及签名数据
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/29
 */
@Data
public class EncryptAndSignatureDto<E, P> {

    /**
     * 应用ID
     */
    private String appId;
    /**
     * 数据签名
     */
    private String signature;
    /**
     * 对称密钥密文
     */
    private String encryptedKey;
    /**
     * 数据密文
     */
    private E encryptedData;

    /**
     * 明文数据
     */
    private P plainData;


    public EncryptAndSignatureDto() {
    }

    public EncryptAndSignatureDto(String appId, String signature, String encryptedKey) {
        this.appId = appId;
        this.signature = signature;
        this.encryptedKey = encryptedKey;
    }

    public EncryptAndSignatureDto(String appId, String signature, String encryptedKey, E encryptedData) {
        this.appId = appId;
        this.signature = signature;
        this.encryptedKey = encryptedKey;
        this.encryptedData = encryptedData;
    }

    public EncryptAndSignatureDto(String appId, P plainData) {
        this.plainData = plainData;
        this.appId = appId;
    }

    public EncryptAndSignatureDto(EncryptAndSignatureDto<E, P> that) {
        this.appId = that.getAppId();
        this.signature = that.getSignature();
        this.encryptedKey = that.getEncryptedKey();
        this.encryptedData = that.getEncryptedData();
        this.plainData = that.getPlainData();
    }

}
