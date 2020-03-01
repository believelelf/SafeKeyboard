package com.weiquding.safeKeyboard.common.domain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 密钥库证书相关属性文件
 * @author believeyourself
 */
@Data
@ConfigurationProperties(prefix = "cipher")
public class CipherPathProperties {


    /**
     * 安全键盘密钥库
     */
    private String safeKeyboardKeyStorePath;

    /**
     * 安全键盘证书
     */
    private String safeKeyboardCerPath;

    /**
     * 客户端RSA私钥文件路径
     */
    private String clientRsaPrivateKeyPkcs1Path;

    /**
     * 服务端RSA公钥文件路径
     */
    private String serverRsaPublicKeyPkcs8Path;

    /**
     * 服务端RSA私钥文件路径
     */
    private String serverRsaPrivateKeyPkcs1Path;

    /**
     * 客户端RSA公钥文件路径
     */
    private String clientRsaPublicKeyPkcs8Path;

    /**
     * 安全键盘密钥库密码
     */
    private String storePass;
    /**
     * 安全键盘密钥库KEY密码
     */
    private String keyPass;

    /**
     * key别名
     */
    private String alias;

    /**
     * PBKDF2 slat
     */
    private String slat;

    /**
     * 应用密钥
     */
    private String appSecret;


}
