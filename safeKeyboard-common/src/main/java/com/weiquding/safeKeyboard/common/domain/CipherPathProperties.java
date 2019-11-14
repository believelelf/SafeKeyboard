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



}
