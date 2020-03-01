package com.weiquding.safeKeyboard.common.util;

import com.weiquding.safeKeyboard.common.cache.KeyInstance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;

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
     *
     * @return 加密客户端随机数
     */
    public static RNCAndPMS generateRNCAndPMS() {
        byte[] RNC = RandomUtil.generateRandomBytes(32);
        byte[] PMS = RandomUtil.generateRandomBytes(48);
        byte[] seed = new byte[80];
        System.arraycopy(RNC, 0, seed, 0, RNC.length);
        System.arraycopy(PMS, 0, seed, RNC.length, PMS.length);
        byte[] bytes = RSAUtil.encryptByRSAPublicKey(KeyInstance.RSA_PUBLIC_KEY, seed);
        return new RNCAndPMS(
                MyBase64.getEncoder().encodeToString(RNC),
                MyBase64.getEncoder().encodeToString(PMS),
                MyBase64.getEncoder().encodeToString(bytes)
        );
    }

    @Data
    public static class RNCAndPMS {
        private String rnc;
        private String pms;
        private String rns;
        private String cipherText;

        public RNCAndPMS() {
        }

        public RNCAndPMS(String rnc, String pms, String cipherText) {
            this.rnc = rnc;
            this.pms = pms;
            this.cipherText = cipherText;
        }

        public RNCAndPMS(String rnc, String pms, String rns, String cipherText) {
            this.rnc = rnc;
            this.pms = pms;
            this.rns = rns;
            this.cipherText = cipherText;
        }
    }


    /***
     * 生成服务端随机数并进行base64,再生成对应签名
     * @return base64随机数及签名
     */
    public static RNSAndSign generateRNSAndSign() {
        byte[] seed = RandomUtil.generateRandomBytes(32);
        byte[] base64 = MyBase64.getEncoder().encode(seed);
        byte[] sign = RSAUtil.signByRSAPrivateKey(KeyInstance.RSA_PRIVATE_KEY, base64);
        return new RNSAndSign(
                new String(base64),
                MyBase64.getEncoder().encodeToString(sign)
        );
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RNSAndSign {
        private String rns;
        private String sign;
    }

}
