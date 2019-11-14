package com.weiquding.safeKeyboard.common.cache;

import com.weiquding.safeKeyboard.common.domain.CipherPathProperties;
import com.weiquding.safeKeyboard.common.util.KeystoreUtil;

import javax.annotation.PostConstruct;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * 缓存key实例
 * @author believeyourself
 */
public class KeyInstance {

    private  CipherPathProperties properties;

    public KeyInstance(CipherPathProperties properties){
        this.properties = properties;
    }

    /**
     * 密钥库
     */
    public static KeyStore KEY_STORE;
    /**
     * RSA私钥
     */
    public static RSAPrivateKey RSA_PRIVATE_KEY;
    /**
     * 服务端证书
     */
    public static Certificate SERVER_CERTIFICATE;
    /**
     * 客户端证书
     */
    public static Certificate CLIENT_CERTIFICATE;
    /**
     * 客户端公钥
     */
    public static RSAPublicKey RSA_PUBLIC_KEY;

    @PostConstruct
    public void init(){
        KEY_STORE = KeystoreUtil.loadKeyStore(properties.getSafeKeyboardKeyStorePath(), properties.getStorePass());
        RSA_PRIVATE_KEY = KeystoreUtil.getRSAPrivateKey(KEY_STORE, properties.getAlias(), properties.getKeyPass());
        SERVER_CERTIFICATE = KeystoreUtil.getCertificate(KEY_STORE, properties.getAlias());
        CLIENT_CERTIFICATE = KeystoreUtil.loadCertificate(properties.getSafeKeyboardCerPath());
        RSA_PUBLIC_KEY = KeystoreUtil.getRSAPublicKey(CLIENT_CERTIFICATE);
    }
}
