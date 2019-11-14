package com.weiquding.safeKeyboard.common.util;

import com.weiquding.safeKeyboard.common.exception.CipherRuntimeException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * AES工具
 *
 * @author believeyourself
 */
public class AESUtil {

    private static final String ALGORITHM = "AES";

    private static final String ALGORITHM_PADDING = "AES/GCM/NoPadding";

    private static final int AES_KEY_SIZE = 256;

    private static final int GCM_IV_LENGTH = 12;

    private static final int GCM_TAG_LENGTH = 16;



    /**
     * 获取向量
     * @return 长度为12的向量
     */
    public static byte[] ivParameter(){
        return RandomUtil.generateRandomBytes(GCM_IV_LENGTH);
    }


    /**
     * 生成指定位数的AES密钥
     *
     * @param bitLen 位数
     * @return AES密钥
     */
    public static SecretKey generateAESKey(int bitLen) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            SecureRandom random = new SecureRandom();
            keyGenerator.init(bitLen, random);
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new CipherRuntimeException("An error occurred while generating the AES key", e);
        }
    }

    /**
     * 从指定encoded恢复指定位数的AES密钥
     *
     * @param bitLen  位数
     * @param encoded 原密钥数据
     * @return AES密钥
     */
    public static SecretKey generateAESKey(int bitLen, byte[] encoded) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            SecureRandom random = new SecureRandom();
            random.setSeed(encoded);
            keyGenerator.init(bitLen, random);
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new CipherRuntimeException("An error occurred while generating the AES key", e);
        }
    }

    /**
     * 使用AES/GCM/NoPadding进行解密
     *
     * @param encoded   密钥数据
     * @param iv        向量
     * @param plainData 数据
     * @return 解密后数据
     */
    public static byte[] decryptByAESKey(byte[] encoded, byte[] iv, byte[] plainData) {
        return decryptByAESKey(generateAESKey(AES_KEY_SIZE, encoded), iv, plainData);
    }

    /**
     * 使用AES/GCM/NoPadding进行解密
     *
     * @param key       密钥
     * @param iv        向量
     * @param plainData 数据
     * @return 解密后数据
     */
    public static byte[] decryptByAESKey(SecretKey key, byte[] iv, byte[] plainData) {
        try {
            // Get Cipher Instance
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            // Create SecretKeySpec
            SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), ALGORITHM);
            // Create GCMParameterSpec
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            // Initialize Cipher for ENCRYPT_MODE
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);
            // Perform Encryption
            return cipher.doFinal(plainData);
        } catch (Exception e) {
            throw new CipherRuntimeException("An error occurred while using the AES key for decryption", e);
        }
    }

    /**
     * 使用AES/GCM/NoPadding进行加密
     *
     * @param encoded    密钥
     * @param iv         向量
     * @param cipherData 加密数据
     * @return 解密后数据
     */
    public static byte[] encryptByAESKey(byte[] encoded, byte[] iv, byte[] cipherData) {
        return encryptByAESKey(generateAESKey(AES_KEY_SIZE, encoded), iv, cipherData);
    }


    /**
     * 使用AES/GCM/NoPadding进行加密
     *
     * @param key        密钥
     * @param iv         向量
     * @param cipherData 加密数据
     * @return 解密后数据
     */
    public static byte[] encryptByAESKey(SecretKey key, byte[] iv, byte[] cipherData) {
        try {
            // Get Cipher Instance
            Cipher cipher = Cipher.getInstance(ALGORITHM_PADDING);

            // Create SecretKeySpec
            SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), ALGORITHM);

            // Create GCMParameterSpec
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);

            // Initialize Cipher for DECRYPT_MODE
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);

            // Perform Decryption
            return cipher.doFinal(cipherData);
        } catch (Exception e) {
            throw new CipherRuntimeException("An error occurred while using the AES key for encryption", e);
        }
    }


}
