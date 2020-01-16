package com.weiquding.safeKeyboard.common.util;

import com.weiquding.safeKeyboard.common.exception.SafeBPError;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * HMAC摘要工具类
 *
 * @author believeyourself
 */
public class HmacUtil {

    public static final String HMAC_SHA_256 = "HmacSHA256";

    /**
     * 获取mac实例
     *
     * @param algorithm 算法
     * @param key       密钥
     * @return Mac
     */
    public static Mac getMacInstance(String algorithm, byte[] key) {
        if (key == null) {
            throw new IllegalArgumentException("Null key");
        } else {
            try {
                SecretKeySpec keySpec = new SecretKeySpec(key, algorithm);
                Mac mac = Mac.getInstance(algorithm);
                mac.init(keySpec);
                return mac;
            } catch (Exception e) {
                throw SafeBPError.MAC_INSTANCE.getInfo().initialize(e);
            }
        }
    }
}