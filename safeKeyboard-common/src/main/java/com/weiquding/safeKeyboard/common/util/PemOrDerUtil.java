package com.weiquding.safeKeyboard.common.util;

import com.weiquding.safeKeyboard.common.exception.BaseBPError;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 从PEM或者DER编码格式文件读取公钥与私钥
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/12
 */
public class PemOrDerUtil {

    private static final String ALGORITHM = "RSA";

    private PemOrDerUtil() {
    }

    /**
     * 从指定路径以PEMParser方式加载无加密PKCS#1 PEM 格式的私钥
     * PKCS#1 PEM (-----BEGIN RSA PRIVATE KEY-----)
     *
     * @param fileName 文件路径
     * @return RSAPrivateKey
     */
    public static PrivateKey readUnencryptedPKCS1PEMRSAPrivateKeyByPEMParser(String fileName) {
        return readPEMRSAPrivateKeyByPEMParser(fileName, null);

    }

    /**
     * 从指定路径以PEMParser方式加载加密PKCS#1 PEM 格式的私钥
     * PKCS#1 PEM (-----BEGIN RSA PRIVATE KEY-----)
     *
     * @param fileName 文件路径
     * @param password 密码
     * @return RSAPrivateKey
     */
    public static PrivateKey readEncryptedPKCS1PEMRSAPrivateKeyByPEMParser(String fileName, String password) {
        return readPEMRSAPrivateKeyByPEMParser(fileName, password);

    }

    /**
     * 从指定路径以PEMParser方式加载无加密PKCS#8 PEM 格式的私钥
     * PKCS#8 PEM (-----BEGIN PRIVATE KEY-----)
     *
     * @param fileName 文件路径
     * @return RSAPrivateKey
     */
    public static PrivateKey readUnencryptedPKCS8PEMRSAPrivateKeyByPEMParser(String fileName) {
        return readPEMRSAPrivateKeyByPEMParser(fileName, null);

    }

    /**
     * 从指定路径以PEMParser方式加载加密PKCS#8 PEM 格式的私钥
     * PKCS#8 PEM (-----BEGIN PRIVATE KEY-----)
     *
     * @param fileName 文件路径
     * @param password 密码
     * @return RSAPrivateKey
     */
    public static PrivateKey readEncryptedPKCS8PEMRSAPrivateKeyByPEMParser(String fileName, String password) {
        return readPEMRSAPrivateKeyByPEMParser(fileName, password);

    }

    /**
     * 从指定路径以PEMParser方式加载PEM格式的私钥
     * PKCS#1 PEM (-----BEGIN RSA PRIVATE KEY-----)
     * PKCS#8 PEM (-----BEGIN PRIVATE KEY-----)
     * PKCS#8 DER (binary)
     *
     * @param fileName 文件路径
     * @param password 密码
     * @return RSAPrivateKey
     */
    public static PrivateKey readPEMRSAPrivateKeyByPEMParser(String fileName, String password) {
        // 添加Provider
        Security.addProvider(new BouncyCastleProvider());

        // 读取私钥文件
        try (
                PEMParser pemParser = new PEMParser(new FileReader(fileName));
        ) {
            Object object = pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");

            KeyPair keyPair = null;
            if (object instanceof PEMEncryptedKeyPair) {
                if (password == null) {
                    throw new IllegalArgumentException("password must not be null");
                }
                // PKCS#1 PEM (-----BEGIN RSA PRIVATE KEY-----) 加密私钥，提供密码进行解密
                PEMEncryptedKeyPair pemEncryptedKeyPair = (PEMEncryptedKeyPair) object;
                PEMDecryptorProvider decryptorProvider = new JcePEMDecryptorProviderBuilder().build(password.toCharArray());
                keyPair = converter.getKeyPair(pemEncryptedKeyPair.decryptKeyPair(decryptorProvider));
                return keyPair.getPrivate();
            } else if (object instanceof PEMKeyPair) {
                // PKCS#1 PEM (-----BEGIN RSA PRIVATE KEY-----) 未加密私钥
                PEMKeyPair pemKeyPair = (PEMKeyPair) object;
                keyPair = converter.getKeyPair(pemKeyPair);
                return keyPair.getPrivate();
            } else if (object instanceof PKCS8EncryptedPrivateKeyInfo) {
                if (password == null) {
                    throw new IllegalArgumentException("password must not be null");
                }
                //PKCS#8 PEM (-----BEGIN PRIVATE KEY-----) 加密私钥，提供密码进行解密
                PKCS8EncryptedPrivateKeyInfo pkcs8EncryptedPrivateKeyInfo = (PKCS8EncryptedPrivateKeyInfo) object;
                // decrypt and convert key
                InputDecryptorProvider decryptionProv = new JceOpenSSLPKCS8DecryptorProviderBuilder().build(password.toCharArray());
                PrivateKeyInfo keyInfo = pkcs8EncryptedPrivateKeyInfo.decryptPrivateKeyInfo(decryptionProv);
                return converter.getPrivateKey(keyInfo);
            } else if (object instanceof PrivateKeyInfo) {
                //PKCS#8 PEM (-----BEGIN PRIVATE KEY-----) 未加密私钥
                PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) object;
                return converter.getPrivateKey(privateKeyInfo);
            } else {
                throw new IllegalStateException("Illegal State: " + object);
            }
        } catch (IOException | OperatorCreationException | PKCSException e) {
            throw BaseBPError.READING_RSAPRIVATEKEY.getInfo().initialize(e);
        }
    }

    /**
     * 从指定路径以PEMParser方式加载PEM格式的公钥
     * PKCS#1 PEM (-----BEGIN RSA PUBLIC KEY-----)
     * PKCS#8 PEM (-----BEGIN PUBLIC KEY-----)
     *
     * @param fileName 文件名
     * @return PublicKey
     */
    public static PublicKey readPEMRSAPublicKeyByPEMParser(String fileName) {
        // 添加Provider
        Security.addProvider(new BouncyCastleProvider());

        // 读取私钥文件
        try (
                PEMParser pemParser = new PEMParser(new FileReader(fileName));
        ) {
            Object object = pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
            if (object instanceof SubjectPublicKeyInfo) {
                // PKCS#1 or PKCS#8
                SubjectPublicKeyInfo subjectPublicKeyInfo = (SubjectPublicKeyInfo) object;
                return converter.getPublicKey(subjectPublicKeyInfo);
            } else {
                throw new IllegalStateException("Illegal State: " + object);
            }
        } catch (IOException e) {
            throw BaseBPError.READING_RSAPUBLICKEY.getInfo().initialize(e);
        }
    }

    /**
     * 从指定路径加载 PKCS#8 DER格式的未加密私钥
     * PKCS#8 DER (binary)
     *
     * @param fileName 文件名
     * @return PrivateKey
     */
    public static PrivateKey readUnencryptedPKCS8DERRSAPrivateKey(String fileName) {
        try {
            byte[] keyBytes = Files.readAllBytes(Paths.get(fileName));
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
            return kf.generatePrivate(spec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw BaseBPError.READING_RSAPRIVATEKEY.getInfo().initialize(e);
        }
    }


    /**
     * 从指定路径加载 PKCS#8 DER格式的加密私钥
     * PKCS#8 DER (binary)
     *
     * @param fileName 文件名
     * @return PrivateKey
     */
    public static PrivateKey readEncryptedPKCS8DERRSAPrivateKey(String fileName, String password) {
        try {
            // 添加Provider
            Security.addProvider(new BouncyCastleProvider());
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
            byte[] keyBytes = Files.readAllBytes(Paths.get(fileName));
            PKCS8EncryptedPrivateKeyInfo pkcs8EncryptedPrivateKeyInfo = new PKCS8EncryptedPrivateKeyInfo(keyBytes);
            // decrypt and convert key
            InputDecryptorProvider decryptionProv = new JceOpenSSLPKCS8DecryptorProviderBuilder().build(password.toCharArray());
            PrivateKeyInfo keyInfo = pkcs8EncryptedPrivateKeyInfo.decryptPrivateKeyInfo(decryptionProv);
            return converter.getPrivateKey(keyInfo);
        } catch (IOException | OperatorCreationException | PKCSException e) {
            throw BaseBPError.READING_RSAPRIVATEKEY.getInfo().initialize(e);
        }
    }

    /**
     * 从指定路径加载 PKCS#8 DER格式的未加密公钥
     * PKCS#8 DER (binary)
     *
     * @param fileName 文件名
     * @return PublicKey
     */
    public static PublicKey readUnencryptedPKCS8DERRSAPublicKey(String fileName) {
        try {
            byte[] keyBytes = Files.readAllBytes(Paths.get(fileName));
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
            return kf.generatePublic(spec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw BaseBPError.READING_RSAPUBLICKEY.getInfo().initialize(e);
        }
    }

}
