package com.weiquding.safeKeyboard.common.util;

import com.weiquding.safeKeyboard.common.cache.KeyInstance;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/**
 * 随机数生成
 *
 * @author believeyourself
 */
public class RandomUtil {

    /**
     * 获取指定长度随机数
     *
     * @param length 长度
     * @return 随机数字节
     */
    public static byte[] generateRandomBytes(int length) {
        byte[] bytes = new byte[length];
        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);
        return bytes;
    }

    /**
     * 生成客户端随机数，先用RSA公钥加密，再base64编码
     * @return 加密客户端随机数
     */
    public static Map<String, String> generateRNCAndPMS(){
        byte[] RNC = RandomUtil.generateRandomBytes(32);
        byte[] PMS = RandomUtil.generateRandomBytes(48);
        byte[] seed = new byte[80];
        System.arraycopy(RNC, 0, seed, 0, RNC.length);
        System.arraycopy(PMS, 0, seed, RNC.length, PMS.length);
        byte[] bytes = RSAUtil.encryptByRSAPublicKey(KeyInstance.RSA_PUBLIC_KEY, seed);
        Map<String, String> map = new HashMap<>();
        map.put("RNC", Base64.getEncoder().encodeToString(RNC));
        map.put("PMS", Base64.getEncoder().encodeToString(PMS));
        map.put("cipherText", Base64.getEncoder().encodeToString(bytes));
        return map;
    }


    /***
     * 生成服务端随机数并进行base64,再生成对应签名
     * @return base64随机数及签名
     */
    public static Map<String, String> generateRNSAndSign(){
        Map<String,String> map = new HashMap<>();
        byte[] seed = RandomUtil.generateRandomBytes(32);
        byte[] base64 = Base64.getEncoder().encode(seed);
        byte[] sign = RSAUtil.signByRSAPrivateKey(KeyInstance.RSA_PRIVATE_KEY, base64);
        map.put("RNS", new String(base64));
        map.put("sign", Base64.getEncoder().encodeToString(sign));
        return map;
    }

}
