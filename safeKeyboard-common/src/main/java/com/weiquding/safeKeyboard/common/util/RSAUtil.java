package com.weiquding.safeKeyboard.common.util;

import com.weiquding.safeKeyboard.common.exception.BaseBPError;

import javax.crypto.Cipher;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * RSA算法相关实现
 * 私钥签名 公钥验签
 * 公钥加密 公钥解密
 *
 * @author believeyourself
 */
public class RSAUtil {

    private static final String ALGORITHM = "RSA/ECB/PKCS1Padding";

    /**
     * 签名算法
     */
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    /**
     * RSA私钥签名
     *
     * @param privateKey 私钥
     * @param data       待签名文本字节
     * @return 签名
     */
    public static byte[] signByRSAPrivateKey(RSAPrivateKey privateKey, byte[] data) {
        try {
            Signature sign = Signature.getInstance(SIGNATURE_ALGORITHM);
            sign.initSign(privateKey);
            sign.update(data);
            return sign.sign();
        } catch (Exception e) {
            throw BaseBPError.SIGNING.getInfo().initialize(e);
        }
    }

    /**
     * RSA公钥验签
     *
     * @param publicKey 公钥
     * @param data      原数据文本字节
     * @param sign      签名
     * @return 是否验证通过
     */
    public static boolean verifySignByRSAPublicKey(RSAPublicKey publicKey, byte[] data, byte[] sign) {
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(data);
            return signature.verify(sign);
        } catch (Exception e) {
            throw BaseBPError.VERIFY_SIGN.getInfo().initialize(e);
        }
    }

    /**
     * RSA公钥加密
     *
     * @param publicKey 公钥
     * @param data      明文数据
     * @return 加密数据
     */
    public static byte[] encryptByRSAPublicKey(RSAPublicKey publicKey, byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] bytes = cipher.doFinal(data);
            return bytes;
        } catch (Exception e) {
            throw BaseBPError.RSA_ENCRYPTION.getInfo().initialize(e);
        }
    }

    /**
     * RSA私钥解密
     *
     * @param privateKey 私钥
     * @param data       明文数据
     * @return 解密数据
     */
    public static byte[] decryptByRSAPrivateKey(RSAPrivateKey privateKey, byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] bytes = cipher.doFinal(data);
            return bytes;
        } catch (Exception e) {
            throw BaseBPError.RSA_DECRYPTION.getInfo().initialize(e);
        }
    }

}
