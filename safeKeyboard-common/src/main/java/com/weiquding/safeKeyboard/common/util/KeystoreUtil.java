package com.weiquding.safeKeyboard.common.util;

import com.weiquding.safeKeyboard.common.exception.BaseBPError;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * 密钥库及证书操作
 *
 * @author believeyourself
 */
public class KeystoreUtil {

    /**
     * Changes the password used to protect the integrity of the keystore contents.
     */
    private static final String STORE_TYPE = "PKCS12";


    /**
     * Loading a Particular Keystore into Memory
     *
     * @param path      密钥库地址
     * @param storePass 密钥库密码
     * @return KeyStore
     */
    public static KeyStore loadKeyStore(String path, String storePass) {
        try (
                InputStream inputStream = new FileInputStream(new File(path));
        ) {
            KeyStore keyStore = KeyStore.getInstance(STORE_TYPE);
            keyStore.load(inputStream, storePass.toCharArray());
            return keyStore;
        } catch (Exception e) {
            throw BaseBPError.KEYSTORE_INSTANCE.getInfo().initialize(e);
        }
    }

    /**
     * 获取RSAPrivateKey实例
     *
     * @param keyStore 密钥库
     * @param alias    key别名
     * @param keyPass  key密钥
     * @return RSAPrivateKey
     */
    public static RSAPrivateKey getRSAPrivateKey(KeyStore keyStore, String alias, String keyPass) {
        try {
            return (RSAPrivateKey) keyStore.getKey(alias, keyPass.toCharArray());
        } catch (Exception e) {
            throw BaseBPError.RSA_PRIVATEKEY_INSTANCE.getInfo().initialize(e);
        }
    }

    /**
     * 获取Certificate实例
     *
     * @param keyStore 密钥库
     * @param alias    key别名
     * @return Certificate实例
     */
    public static Certificate getCertificate(KeyStore keyStore, String alias) {
        try {
            return keyStore.getCertificate(alias);
        } catch (KeyStoreException e) {
            throw BaseBPError.CERTIFICATE_INSTANCE.getInfo().initialize(e);
        }
    }

    /**
     * 加载RSA证书
     *
     * @param path 证书路径
     * @return RSA证书
     */
    public static Certificate loadCertificate(String path) {
        try (
                FileInputStream fis = new FileInputStream(path);
                BufferedInputStream bis = new BufferedInputStream(fis);
        ) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return cf.generateCertificate(bis);
        } catch (Exception e) {
            throw BaseBPError.CERTIFICATE_LOADING.getInfo().initialize(e);
        }
    }

    /**
     * 获取RSA公钥
     *
     * @param certificate 证书
     * @return RSA公钥
     */
    public static RSAPublicKey getRSAPublicKey(Certificate certificate) {
        return (RSAPublicKey) certificate.getPublicKey();
    }


}