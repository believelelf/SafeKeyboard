package com.weiquding.safeKeyboard.common.util;

import javax.crypto.Mac;
import java.util.Arrays;

/**
 * The pseudo random function
 *
 * @author believeyourself
 */
public class PRFUtil {

    /**
     * master secret
     */
    private static final byte[] MASTER_LABEL = new byte[]{109, 97, 115, 116, 101, 114, 32, 115, 101, 99, 114, 101, 116};

    /**
     * key expansion
     */
    private static final byte[] KEY_BLOCK_LABEL = {107, 101, 121, 32, 101, 120, 112, 97, 110, 115, 105, 111, 110};

    /**
     * 生成伪随机数
     *
     * @param key       密钥
     * @param label     标签
     * @param seed      种子
     * @param numBlocks 迭代次数
     * @return 伪随机数
     */
    public static byte[] prf(byte[] key, byte[] label, byte[] seed, int numBlocks) {
        if (key == null
                || label == null
                || seed == null
                || numBlocks == 0
        ) {
            throw new IllegalArgumentException();
        }
        byte[] newSeed = new byte[label.length + seed.length];
        System.arraycopy(label, 0, newSeed, 0, label.length);
        System.arraycopy(seed, 0, newSeed, label.length, seed.length);
        Mac mac = HmacUtil.getMacInstance(HmacUtil.HMAC_SHA_256, key);
        byte[] digest = mac.doFinal(newSeed);
        byte[] output = new byte[0];
        for (int i = 0; i < numBlocks; i++) {
            byte[] toDigest = new byte[digest.length + newSeed.length];
            System.arraycopy(digest, 0, toDigest, 0, digest.length);
            System.arraycopy(newSeed, 0, toDigest, digest.length, newSeed.length);
            byte[] out = mac.doFinal(toDigest);

            byte[] newOutput = new byte[output.length + out.length];
            System.arraycopy(output, 0, newOutput, 0, output.length);
            System.arraycopy(out, 0, newOutput, output.length, out.length);
            output = newOutput;
            digest = mac.doFinal(digest);
        }
        return output;
    }

    /**
     * Compute the master secret from the premaster secret
     * 生成Master Secret
     *
     * @param PMS pre-master secret
     * @param RNC random number of client
     * @param RNS random number of server
     * @return Master Secret
     */
    public static byte[] generateMasterSecret(byte[] PMS, byte[] RNC, byte[] RNS) {
        if (PMS == null
                || RNC == null
                || RNS == null
        ) {
            throw new IllegalArgumentException();
        }
        byte[] seed = new byte[RNC.length + RNS.length];
        System.arraycopy(RNC, 0, seed, 0, RNC.length);
        System.arraycopy(RNS, 0, seed, RNC.length, RNS.length);
        byte[] randoms = prf(PMS, MASTER_LABEL, seed, 2);
        byte[] ms = new byte[48];
        System.arraycopy(randoms, 0, ms, 0, ms.length);
        return ms;
    }

    /**
     * Generate the key block
     * 生成以下密钥数据
     * keyBlock[0]:客户端Mac摘要密钥：clientMacKey[32]
     * keyBlock[1]:服务端Mac摘要密钥：serverMacKey[32]
     * keyBlock[2]:客户端AES对称加密密钥：clientWriteKey[32]
     * keyBlock[3]:服务端AES对称加密密钥：serverWriteKey[32]
     * keyBlock[4]:客户端AES对称加密向量：clientWriteIV[16]
     * keyBlock[5]:服务端AES对称加密向量：serverWriteIV[16]
     *
     * @param MS  master secret
     * @param RNC random number of client
     * @param RNS random number of server
     * @return key block
     */
    public static byte[][] generateKeyBlock(byte[] MS, byte[] RNC, byte[] RNS) {
        if (MS == null
                || RNC == null
                || RNS == null
        ) {
            throw new IllegalArgumentException();
        }
        byte[] seed = new byte[RNC.length + RNS.length];
        System.arraycopy(RNC, 0, seed, 0, RNC.length);
        System.arraycopy(RNS, 0, seed, RNC.length, RNS.length);
        byte[] randoms = prf(MS, KEY_BLOCK_LABEL, seed, 5);
        byte[][] keyBlock = new byte[6][];
        keyBlock[0] = Arrays.copyOfRange(randoms, 0, 32);
        keyBlock[1] = Arrays.copyOfRange(randoms, 32, 64);
        keyBlock[2] = Arrays.copyOfRange(randoms, 64, 96);
        keyBlock[3] = Arrays.copyOfRange(randoms, 96, 128);
        keyBlock[4] = Arrays.copyOfRange(randoms, 128, 144);
        keyBlock[5] = Arrays.copyOfRange(randoms, 144, 160);
        return keyBlock;
    }

    /**
     * 生成key block
     *
     * @param PMS base64 pms
     * @param RNC base64 rnc
     * @param RNS base64 rns
     * @return
     */
    public static byte[][] generateKeyBlock(String PMS, String RNC, String RNS) {
        byte[] RNCBytes = Base64.getDecoder().decode(RNC);
        byte[] RNSBytes = Base64.getDecoder().decode(RNS);
        byte[] MS = generateMasterSecret(Base64.getDecoder().decode(PMS), RNCBytes, RNSBytes);
        return generateKeyBlock(MS, RNCBytes, RNSBytes);
    }

}