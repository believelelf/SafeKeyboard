package com.weiquding.safeKeyboard.common.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * AESUtils测试工具
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/12
 */
public class AESUtilTests {

    private byte[] encoded = null;
    private byte[] gcmIV = null;
    private byte[] cbcIV = null;
    private byte[] data = null;

    @Before
    public void setup() {
        encoded = AESUtil.initAESKey(256);
        gcmIV = AESUtil.AES_256_GCM_NoPadding.ivParameter();
        cbcIV = AESUtil.AES_256_CBC_NoPadding.ivParameter();
        // 为测试NoPadding模式，输入数据长度必须为16字节的倍数
        data = "passwordpassword".getBytes(StandardCharsets.UTF_8);

    }

    @Test
    public void testAES_256_GCM_NoPadding() {
        byte[] encryptByAESKey = AESUtil.AES_256_GCM_NoPadding.encryptByAESKey(encoded, gcmIV, data);
        byte[] decryptByAESKey = AESUtil.AES_256_GCM_NoPadding.decryptByAESKey(encoded, gcmIV, encryptByAESKey);
        Assert.assertTrue(Arrays.equals(data, decryptByAESKey));

    }

    @Test
    public void testAES_256_CBC_NoPadding() {
        byte[] encryptByAESKey = AESUtil.AES_256_CBC_NoPadding.encryptByAESKey(encoded, cbcIV, data);
        byte[] decryptByAESKey = AESUtil.AES_256_CBC_NoPadding.decryptByAESKey(encoded, cbcIV, encryptByAESKey);
        Assert.assertTrue(Arrays.equals(data, decryptByAESKey));
    }

    @Test
    public void testAES_256_CBC_NoPaddingDefaultIV() {
        byte[] encryptByAESKey = AESUtil.AES_256_CBC_NoPadding.encryptByAESKey(encoded, data);
        byte[] decryptByAESKey = AESUtil.AES_256_CBC_NoPadding.decryptByAESKey(encoded, encryptByAESKey);
        Assert.assertTrue(Arrays.equals(data, decryptByAESKey));
    }

    @Test
    public void testAES_256_CBC_PKCS5Padding() {
        byte[] encryptByAESKey = AESUtil.AES_256_CBC_PKCS5Padding.encryptByAESKey(encoded, cbcIV, data);
        byte[] decryptByAESKey = AESUtil.AES_256_CBC_PKCS5Padding.decryptByAESKey(encoded, cbcIV, encryptByAESKey);
        Assert.assertTrue(Arrays.equals(data, decryptByAESKey));
    }

    @Test
    public void testAES_256_CBC_PKCS5PaddingDefaultIV() {
        byte[] encryptByAESKey = AESUtil.AES_256_CBC_PKCS5Padding.encryptByAESKey(encoded, data);
        byte[] decryptByAESKey = AESUtil.AES_256_CBC_PKCS5Padding.decryptByAESKey(encoded, encryptByAESKey);
        Assert.assertTrue(Arrays.equals(data, decryptByAESKey));
    }

    @Test
    public void testAES_256_CBC_PKCS7Padding() {
        byte[] encryptByAESKey = AESUtil.AES_256_CBC_PKCS7Padding.encryptByAESKey(encoded, cbcIV, data);
        byte[] decryptByAESKey = AESUtil.AES_256_CBC_PKCS7Padding.decryptByAESKey(encoded, cbcIV, encryptByAESKey);
        Assert.assertTrue(Arrays.equals(data, decryptByAESKey));
    }

    @Test
    public void testAES_256_CBC_PKCS7PaddingDefaultIV() {
        byte[] encryptByAESKey = AESUtil.AES_256_CBC_PKCS7Padding.encryptByAESKey(encoded, data);
        byte[] decryptByAESKey = AESUtil.AES_256_CBC_PKCS7Padding.decryptByAESKey(encoded, encryptByAESKey);
        Assert.assertTrue(Arrays.equals(data, decryptByAESKey));
    }


    /**
     * Input length must be multiple of 16
     */
    @Test
    public void testAES_256_ECB_NoPadding() {
        byte[] encryptByAESKey = AESUtil.AES_256_ECB_NoPadding.encryptByAESKey(encoded, data);
        byte[] decryptByAESKey = AESUtil.AES_256_ECB_NoPadding.decryptByAESKey(encoded, encryptByAESKey);
        Assert.assertTrue(Arrays.equals(data, decryptByAESKey));
    }

    @Test
    public void testAES_256_ECB_PKCS5Padding() {
        byte[] encryptByAESKey = AESUtil.AES_256_ECB_PKCS5Padding.encryptByAESKey(encoded, data);
        byte[] decryptByAESKey = AESUtil.AES_256_ECB_PKCS5Padding.decryptByAESKey(encoded, encryptByAESKey);
        Assert.assertTrue(Arrays.equals(data, decryptByAESKey));
    }

    @Test
    public void testAES_256_ECB_PKCS7Padding() {
        byte[] encryptByAESKey = AESUtil.AES_256_ECB_PKCS7Padding.encryptByAESKey(encoded, data);
        byte[] decryptByAESKey = AESUtil.AES_256_ECB_PKCS7Padding.decryptByAESKey(encoded, encryptByAESKey);
        Assert.assertTrue(Arrays.equals(data, decryptByAESKey));
    }
}
