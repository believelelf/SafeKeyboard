package com.weiquding.safeKeyboard.common.util;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * PemOrDerUtil 测试类
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/12
 */
public class PemOrDerUtilTests {

    public static final String BASE_PATH = "E:\\Java\\idea_workspaces\\SafeKeyboard\\cipher";

    public static final String PASSWORD = "123456";

    public static final String PRIVATE_UNENCRYPTED_KEY_PKCS1_PEM = BASE_PATH + File.separator + "private_unencrypted_key_pkcs1.pem";

    public static final String PRIVATE_UNENCRYPTED_KEY_PKCS8_PEM = BASE_PATH + File.separator + "private_unencrypted_key_pkcs8.pem";

    public static final String PUBLIC_UNENCRYPTED_KEY_PKCS1_PEM = BASE_PATH + File.separator + "public_unencrypted_key_pkcs1.pem";

    public static final String PUBLIC_UNENCRYPTED_KEY_PKCS8_PEM = BASE_PATH + File.separator + "public_unencrypted_key_pkcs8.pem";

    public static final String PRIVATE_ENCRYPTED_WITHPASS_123456_KEY_PKCS1_PEM = BASE_PATH + File.separator + "private_encrypted_withpass_123456_key_pkcs1.pem";

    public static final String PRIVATE_ENCRYPTED_WITHPASS_123456_KEY_PKCS8_PEM = BASE_PATH + File.separator + "private_encrypted_withpass_123456_key_pkcs8.pem";

    public static final String PUBLIC_ENCRYPTED_WITHOUTPASS_123456_KEY_PKCS1_PEM = BASE_PATH + File.separator + "public_encrypted_withoutpass_123456_key_pkcs1.pem";

    public static final String PUBLIC_ENCRYPTED_WITHOUTPASS_123456_KEY_PKCS8_PEM = BASE_PATH + File.separator + "public_encrypted_withoutpass_123456_key_pkcs8.pem";

    public static final String PRIVATE_UNENCRYPTED_KEY_PKCS8_DER = BASE_PATH + File.separator + "private_unencrypted_key_pkcs8.der";

    public static final String PUBLIC_UNENCRYPTED_KEY_PKCS8_DER = BASE_PATH + File.separator + "public_unencrypted_key_pkcs8.der";

    public static final String PRIVATE_ENCRYPTED_WITHPASS_123456_KEY_PKCS8_DER = BASE_PATH + File.separator + "private_encrypted_withpass_123456_key_pkcs8.der";

    public static final String PUBLIC_ENCRYPTED_WITHOUTPASS_123456_KEY_PKCS8_DER = BASE_PATH + File.separator + "public_encrypted_withoutpass_123456_key_pkcs8.der";

    @Test
    public void testReadUnencryptedRSAPrivateKeyByPEMParser() {
        PrivateKey privateKey = PemOrDerUtil.readUnencryptedPKCS1PEMRSAPrivateKeyByPEMParser(PRIVATE_UNENCRYPTED_KEY_PKCS1_PEM);
        Assert.assertTrue(privateKey instanceof RSAPrivateKey);
    }

    @Test
    public void testReadEncryptedRSAPrivateKeyByPEMParser() {
        PrivateKey privateKey = PemOrDerUtil.readEncryptedPKCS1PEMRSAPrivateKeyByPEMParser(PRIVATE_ENCRYPTED_WITHPASS_123456_KEY_PKCS1_PEM, PASSWORD);
        Assert.assertTrue(privateKey instanceof RSAPrivateKey);
    }

    @Test
    public void testReadUnEncryptedRSAPrivateKeyPKCS8ByPEMParser() {
        PrivateKey privateKey = PemOrDerUtil.readUnencryptedPKCS8PEMRSAPrivateKeyByPEMParser(PRIVATE_UNENCRYPTED_KEY_PKCS8_PEM);
        Assert.assertTrue(privateKey instanceof RSAPrivateKey);
    }

    @Test
    public void testReadEncryptedRSAPrivateKeyPKCS8ByPEMParser() {
        PrivateKey privateKey = PemOrDerUtil.readEncryptedPKCS8PEMRSAPrivateKeyByPEMParser(PRIVATE_ENCRYPTED_WITHPASS_123456_KEY_PKCS8_PEM, PASSWORD);
        Assert.assertTrue(privateKey instanceof RSAPrivateKey);
    }

    @Test
    public void testReadUnencryptedPKCS8DERRSAPrivateKey() {
        PrivateKey privateKey = PemOrDerUtil.readUnencryptedPKCS8DERRSAPrivateKey(PRIVATE_UNENCRYPTED_KEY_PKCS8_DER);
        Assert.assertTrue(privateKey instanceof RSAPrivateKey);
    }

    @Test
    public void testReadEncryptedPKCS8DERRSAPrivateKey() {
        PrivateKey privateKey = PemOrDerUtil.readEncryptedPKCS8DERRSAPrivateKey(PRIVATE_ENCRYPTED_WITHPASS_123456_KEY_PKCS8_DER, PASSWORD);
        Assert.assertTrue(privateKey instanceof RSAPrivateKey);
    }

    @Test
    public void testReadPEMRSAPublicKeyByPEMParser() {
        PublicKey publicKey = PemOrDerUtil.readPEMRSAPublicKeyByPEMParser(PUBLIC_UNENCRYPTED_KEY_PKCS8_PEM);
        Assert.assertTrue(publicKey instanceof RSAPublicKey);
    }

    @Test
    public void testReadPEMRSAPublicKeyByPEMParserWithoutPass() {
        PublicKey publicKey = PemOrDerUtil.readPEMRSAPublicKeyByPEMParser(PUBLIC_ENCRYPTED_WITHOUTPASS_123456_KEY_PKCS8_PEM);
        Assert.assertTrue(publicKey instanceof RSAPublicKey);
    }

    @Test
    public void testReadPKCS1PEMRSAPublicKeyByPEMParser() {
        PublicKey publicKey = PemOrDerUtil.readPEMRSAPublicKeyByPEMParser(PUBLIC_UNENCRYPTED_KEY_PKCS1_PEM);
        Assert.assertTrue(publicKey instanceof RSAPublicKey);
    }

    @Test
    public void testReadPKCS1PEMRSAPublicKeyByPEMParserWithoutPass() {
        PublicKey publicKey = PemOrDerUtil.readPEMRSAPublicKeyByPEMParser(PUBLIC_ENCRYPTED_WITHOUTPASS_123456_KEY_PKCS1_PEM);
        Assert.assertTrue(publicKey instanceof RSAPublicKey);
    }

    @Test
    public void testReadUnencryptedPKCS8DERRSAPublicKey() {
        PublicKey publicKey = PemOrDerUtil.readUnencryptedPKCS8DERRSAPublicKey(PUBLIC_UNENCRYPTED_KEY_PKCS8_DER);
        Assert.assertTrue(publicKey instanceof RSAPublicKey);
    }

    @Test
    public void testReadUnencryptedPKCS8DERRSAPublicKeyWithoutPass() {
        PublicKey publicKey = PemOrDerUtil.readUnencryptedPKCS8DERRSAPublicKey(PUBLIC_ENCRYPTED_WITHOUTPASS_123456_KEY_PKCS8_DER);
        Assert.assertTrue(publicKey instanceof RSAPublicKey);
    }

}
